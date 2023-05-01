package com.example.sudokujetpackcompose.ui.activegame.BuildLogic

import android.content.Context
import com.example.sudokujetpackcompose.common.ProductionDispatcherProvider
import com.example.sudokujetpackcompose.persistemce.*
import com.example.sudokujetpackcompose.persistemce.settingsDataStore
import com.example.sudokujetpackcompose.ui.activegame.ActiveGameContainer
import com.example.sudokujetpackcompose.ui.activegame.ActiveGameLogic
import com.example.sudokujetpackcompose.ui.activegame.ActiveGameViewModel

internal fun buildActiveGameLogic(
    container: ActiveGameContainer,
    viewModel: ActiveGameViewModel,
    context: Context
): ActiveGameLogic {
    return ActiveGameLogic(
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