package com.keikuethas.osemulator.cmd.commands

import com.keikuethas.osemulator.SystemInfo
import com.keikuethas.osemulator.cmd.VFS
import com.keikuethas.osemulator.mvi.CMDResponse
import com.keikuethas.osemulator.mvi.CMDResult
import kotlinx.coroutines.flow.MutableSharedFlow
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object Uptime : TerminalCommand("uptime") {
    override suspend fun invoke(
        vfs: VFS,
        outputFlow: MutableSharedFlow<CMDResponse>,
        args: List<String>
    ) {
        if (!args.isEmpty()) {
            outputFlow.emit(
                CMDResult.InvalidArgument(
                    args.first(),
                    "Команда exit не поддерживает аргументы"
                )
            )

            return
        }

        outputFlow.emit(CMDResult.Print(getUptime()))

    }

    override fun validate(vfs: VFS, args: List<String>): Boolean = args.isEmpty()


    private val timeFmt = DateTimeFormatter.ofPattern("HH:mm:ss")
    private val locale = Locale.US

    fun getUptime(): String {
        val now = Instant.now()
        val time = timeFmt.format(now.atZone(ZoneId.systemDefault()))

        val upStr = format(SystemInfo.uptimeSeconds())
        val users = SystemInfo.loggedInUsers()
        val usersStr = "$users пользовател" + when (users) {
            1 -> "ь"
            2, 3, 4 -> "я"
            else -> "ей"
        }

        return "$time: работает $upStr,  $usersStr"
    }

    private fun format(totalSec: Long): String {
        val days = totalSec / 86400
        val h = (totalSec % 86400) / 3600
        val m = (totalSec % 3600) / 60
        return when {
            days > 0 -> String.format(
                locale, "%s, %2d:%02d",
                when (days) {
                    1L -> "1 день"
                    2L, 3L, 4L -> "$days дня"
                    else -> "$days дней"
                }, h, m
            )

            h > 0 -> String.format(locale, "%2d:%02d", h, m)
            else -> "$m min"
        }
    }
}

