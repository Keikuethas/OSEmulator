package com.keikuethas.osemulator

// commonMain
expect object SystemInfo {
    fun uptimeSeconds(): Long
    fun loggedInUsers(): Int
}