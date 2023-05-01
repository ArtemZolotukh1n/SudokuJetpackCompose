package com.example.sudokujetpackcompose.ui.newgame.buildlogic

import android.content.Context

import com.example.sudokujetpackcompose.common.ProductionDispatcherProvider
import com.example.sudokujetpackcompose.persistemce.*
import com.example.sudokujetpackcompose.persistemce.settingsDataStore
import com.example.sudokujetpackcompose.ui.newgame.NewGameContainer
import com.example.sudokujetpackcompose.ui.newgame.NewGameLogic
import com.example.sudokujetpackcompose.ui.newgame.NewGameViewModel

internal fun buildNewGameLogic(
    container: NewGameContainer,
    viewModel: NewGameViewModel,
    context: Context
): NewGameLogic {
    return NewGameLogic(
        container,
        viewModel,
        GameRepositoryImpl(
            LocalGameStorageImpl(context.filesDir.path),
            LocalSettingsStorageImpl(context.settingsDataStore)
        ),
        LocalStatisticsStorageImpl(
            context.statsDataStore
        ),
        ProductionDispatcherProvider
    )
}