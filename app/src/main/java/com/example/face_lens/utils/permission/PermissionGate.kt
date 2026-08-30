package com.example.face_lens.utils.permission

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.face_lens.utils.findActivity

data class PermissionGateActions(
    val canRequestAgain: Boolean,
    val requestAgain: () -> Unit,
    val openSettings: () -> Unit,
)

@Composable
fun PermissionGate(
    permission: String,
    deniedContent: @Composable (PermissionGateActions) -> Unit,
    pendingContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasPermission by remember(permission) {
        mutableStateOf(context.hasPermission(permission))
    }
    var hasRequestedPermission by rememberSaveable(permission) { mutableStateOf(false) }
    var canRequestAgain by remember(permission) { mutableStateOf(true) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasPermission = granted
        canRequestAgain = activity?.canRequestPermissionAgain(permission) == true
    }

    DisposableEffect(lifecycleOwner, context, permission) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasPermission = context.hasPermission(permission)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(hasPermission, hasRequestedPermission, permission) {
        if (!hasPermission && !hasRequestedPermission) {
            hasRequestedPermission = true
            permissionLauncher.launch(permission)
        }
    }

    when {
        hasPermission -> content()
        hasRequestedPermission -> deniedContent(
            PermissionGateActions(
                canRequestAgain = canRequestAgain,
                requestAgain = { permissionLauncher.launch(permission) },
                openSettings = context::openAppSettings,
            ),
        )
        else -> pendingContent()
    }
}
