package com.keikuethas.osemulator.mvi

import com.keikuethas.osemulator.mvi.usecases.input
import com.keikuethas.osemulator.mvi.usecases.invalidArgument
import com.keikuethas.osemulator.mvi.usecases.invalidCommand
import com.keikuethas.osemulator.mvi.usecases.print

object CMDReducer {
    fun reduce(
        state: CMDWindowState,
        result: CMDResult,
    ): CMDWindowState =
            when (result) {

                is CMDResult.Input -> input(state, result)

                is CMDResult.InvalidArgument ->
                    invalidArgument(state, result)

                is CMDResult.InvalidCommand ->
                    invalidCommand(state, result)

                is CMDResult.Print -> print(state, result)
            }
}