package com.Sasfifu3.Wagraw.core.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberAppFactory(): AppViewModelFactory =
    remember { AppViewModelFactory(ServiceLocator.container()) }
