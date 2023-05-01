package com.example.sudokujetpackcompose.common

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

// Object because I need a thread safe singleton
object ProductionDispatcherProvider : DispatcherProvider {
    override fun provideUIContext(): CoroutineContext {
        return Dispatchers.Main
    }

    override fun provideIOContext(): CoroutineContext {
        return Dispatchers.IO
    }
}