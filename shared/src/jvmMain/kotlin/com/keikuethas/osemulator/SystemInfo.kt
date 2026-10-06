package com.keikuethas.osemulator// jvmMain
import java.io.File
import java.lang.management.ManagementFactory
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

actual object SystemInfo {

    private val osName = System.getProperty("os.name").lowercase()

    actual fun uptimeSeconds(): Long = when {
        osName.contains("win") -> windowsUptimeSeconds()
        osName.contains("linux") -> linuxUptimeSeconds()
        osName.contains("mac") -> macUptimeSeconds()
        else -> fallbackJvmUptime()
    }

    actual fun loggedInUsers(): Int = when {
        osName.contains("win") -> windowsLoggedInUsers()
        else -> unixLoggedInUsers()
    }

    // ---------- Windows ----------

    /**
     * Uptime через WMI (Win32_OperatingSystem.LastBootUpTime).
     * PowerShell медленный (~0.5 сек), но wmic deprecated на новых Windows.
     */
    private fun windowsUptimeSeconds(): Long {
        val boot = windowsBootTimeMillis() ?: return fallbackJvmUptime()
        return (System.currentTimeMillis() - boot) / 1000
    }

    private fun windowsBootTimeMillis(): Long? {
        // Вариант 1: PowerShell + CIM (актуально для Windows 10/11)
        val ps = """
            (Get-CimInstance Win32_OperatingSystem).LastBootUpTime.ToFileTimeUtc()
        """.trimIndent()
        runCommand("powershell", "-NoProfile", "-Command", ps)
            ?.trim()
            ?.toLongOrNull()
            ?.let { fileTime ->
                // FILETIME → unix millis: (fileTime / 10000) - 11644473600000
                return fileTime / 10_000 - 11_644_473_600_000L
            }

        // Вариант 2 (fallback): net statistics workstation
        val out = runCommand("cmd", "/c", "net statistics workstation")
        if (out != null) {
            val line = out.lineSequence().firstOrNull { it.contains("since", ignoreCase = true) }
            if (line != null) {
                val dateStr = line.substringAfter("since", "").trim()
                return try {
                    val fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
                    LocalDateTime.parse(dateStr, fmt)
                        .atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli()
                } catch (_: Exception) { null }
            }
        }
        return null
    }

    private fun windowsLoggedInUsers(): Int {
        val out = runCommand("cmd", "/c", "query user") ?: return 0
        // Первая строка — заголовок, остальные — сессии
        return (out.lines().count { it.isNotBlank() } - 1).coerceAtLeast(0)
    }

    // ---------- Linux ----------

    private fun linuxUptimeSeconds(): Long {
        val f = File("/proc/uptime")
        if (!f.exists()) return fallbackJvmUptime()
        return f.readText().trim().split(" ")[0].toDouble().toLong()
    }

    // ---------- macOS ----------

    private fun macUptimeSeconds(): Long {
        val out = runCommand("sysctl", "-n", "kern.boottime") ?: return fallbackJvmUptime()
        // { sec = 1696591200, usec = 0 } ...
        val sec = Regex("sec\\s*=\\s*(\\d+)").find(out)?.groupValues?.get(1)?.toLongOrNull()
            ?: return fallbackJvmUptime()
        return System.currentTimeMillis() / 1000 - sec
    }

    // ---------- Общие ----------

    private fun unixLoggedInUsers(): Int {
        val out = runCommand("who") ?: return 0
        return out.lines().count { it.isNotBlank() }
    }

    private fun fallbackJvmUptime(): Long =
        ManagementFactory.getRuntimeMXBean().uptime / 1000

    private fun runCommand(vararg cmd: String): String? = try {
        val p = ProcessBuilder(*cmd).redirectErrorStream(true).start()
        val out = p.inputStream.bufferedReader().readText()
        p.waitFor(5, TimeUnit.SECONDS)
        out
    } catch (_: Exception) { null }
}