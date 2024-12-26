package com.humanforce.humanforceandroidengineeringchallenge.presentation.common

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * Created by kervinlevi on 26/12/24
 */
@Composable
fun SimpleToast(stringRes: Int, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(LocalContext.current, stringResource(stringRes), length).show()
}

@Composable
fun SimpleToast(text: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(LocalContext.current, text, length).show()
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocationPermission(onPermissionGranted: () -> Unit, onPermissionDenied: () -> Unit) {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { granted ->
            if (granted.containsValue(true)) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }

    LaunchedEffect(key1 = permissionsState) {
        if (!permissionsState.permissions.first().status.isGranted && !permissionsState.permissions.last().status.isGranted && permissionsState.shouldShowRationale) {
            onPermissionDenied()
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}
