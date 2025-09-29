package com.metrolist.music.ui.player

import androidx.activity.compose.BackHandler
import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.source.ShuffleOrder.DefaultShuffleOrder
import androidx.navigation.NavController
import com.metrolist.music.LocalPlayerConnection
import com.metrolist.music.R
import com.metrolist.music.constants.DarkModeKey
import com.metrolist.music.constants.ListItemHeight
import com.metrolist.music.constants.PlayerBackgroundStyle
import com.metrolist.music.constants.UseNewPlayerDesignKey
import com.metrolist.music.constants.QueueEditLockKey
import com.metrolist.music.extensions.metadata
import com.metrolist.music.extensions.move
import com.metrolist.music.extensions.togglePlayPause
import com.metrolist.music.extensions.toggleRepeatMode
import com.metrolist.music.models.MediaMetadata
import com.metrolist.music.ui.component.ActionPromptDialog
import com.metrolist.music.ui.component.BottomSheet
import com.metrolist.music.ui.component.BottomSheetState
import com.metrolist.music.ui.component.LocalBottomSheetPageState
import com.metrolist.music.ui.component.LocalMenuState
import com.metrolist.music.ui.component.MediaMetadataListItem
import com.metrolist.music.ui.menu.PlayerMenu
import com.metrolist.music.ui.menu.SelectionMediaMetadataMenu
import com.metrolist.music.ui.screens.settings.DarkMode
import com.metrolist.music.ui.utils.ShowMediaInfo
import com.metrolist.music.utils.makeTimeString
import com.metrolist.music.utils.rememberEnumPreference
import com.metrolist.music.utils.rememberPreference
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.math.roundToInt

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Queue(
    state: BottomSheetState,
    playerBottomSheetState: BottomSheetState,
    navController: NavController,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    onBackgroundColor: Color,
    TextBackgroundColor: Color,
    textButtonColor: Color,
    iconButtonColor: Color,
    onShowLyrics: () -> Unit = {},
    pureBlack: Boolean,
    backgroundStyle: PlayerBackgroundStyle,
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val clipboardManager = LocalClipboard.current
    val menuState = LocalMenuState.current
    val bottomSheetPageState = LocalBottomSheetPageState.current

    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isPlaying.collectAsState()
    val repeatMode by playerConnection.repeatMode.collectAsState()

    val currentWindowIndex by playerConnection.currentWindowIndex.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val currentSong by playerConnection.currentSong.collectAsState(initial = null)
    val shuffleModeEnabled by playerConnection.shuffleModeEnabled.collectAsState()

    val isSystemInDarkTheme = isSystemInDarkTheme()
    val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.AUTO)
    val useDarkTheme = remember(darkTheme, isSystemInDarkTheme) {
        if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
    }

    val currentFormat by playerConnection.currentFormat.collectAsState(initial = null)

    val selectedSongs = remember { mutableStateListOf<MediaMetadata>() }
    val selectedItems = remember { mutableStateListOf<Timeline.Window>() }
    var selection by remember { mutableStateOf(false) }

    if (selection) {
        BackHandler {
            selection = false
        }
    }

    var locked by rememberPreference(QueueEditLockKey, defaultValue = true)

    val (useNewPlayerDesign, onUseNewPlayerDesignChange) = rememberPreference(
        UseNewPlayerDesignKey, defaultValue = true
    )

    val snackbarHostState = remember { SnackbarHostState() }
    var dismissJob: Job? by remember { mutableStateOf(null) }

    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var sleepTimerValue by remember { mutableFloatStateOf(30f) }
    val sleepTimerEnabled = remember(
        playerConnection.service.sleepTimer.triggerTime, playerConnection.service.sleepTimer.pauseWhenSongEnd
    ) {
        playerConnection.service.sleepTimer.isActive
    }
    var sleepTimerTimeLeft by remember { mutableLongStateOf(0L) }

    LaunchedEffect(sleepTimerEnabled) {
        if (sleepTimerEnabled) {
            while (isActive) {
                sleepTimerTimeLeft = if (playerConnection.service.sleepTimer.pauseWhenSongEnd) {
                    playerConnection.player.duration - playerConnection.player.currentPosition
                } else {
                    playerConnection.service.sleepTimer.triggerTime - System.currentTimeMillis()
                }
                delay(1000L)
            }
        }
    }

    BottomSheet(
        state = state,
        backgroundColor = Color.Unspecified,
        modifier = modifier,
        collapsedContent = {
            if (useNewPlayerDesign) {
                // New design
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 8.dp)
                        .windowInsetsPadding(
                            WindowInsets.systemBars.only(
                                WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal
                            ),
                        )
                        .background(Color.Black.copy(alpha = 0.1f), CircleShape)
                        .padding(8.dp)
                ) {
                    val buttonSize = 42.dp
                    val iconSize = 24.dp
                    val borderColor = if (useDarkTheme || backgroundStyle == PlayerBackgroundStyle.DEFAULT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                    val disableBorderColor = borderColor.copy(0.45f)

                    OutlinedIconButton(
                        modifier = Modifier
                            .weight(1f)
                            .size(IconButtonDefaults.mediumContainerSize()),
                        onClick = state::expandSoft,
                        shape = ButtonGroupDefaults.connectedLeadingButtonShape,
                        border = BorderStroke(1.5.dp, borderColor),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.queue_music), contentDescription = null, modifier = Modifier.size(IconButtonDefaults.mediumIconSize), tint = borderColor
                        )
                    }

                    OutlinedIconButton(
                        modifier = Modifier
                            .weight(1f)
                            .size(IconButtonDefaults.mediumContainerSize()), onClick = {
                            playerConnection.player.shuffleModeEnabled = !playerConnection.player.shuffleModeEnabled
                        }, shape = ShapeDefaults.Medium, border = BorderStroke(1.5.dp, if (shuffleModeEnabled) borderColor else disableBorderColor)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.shuffle), contentDescription = null, modifier = Modifier.size(IconButtonDefaults.mediumIconSize), tint = if (shuffleModeEnabled) borderColor else disableBorderColor
                        )
                    }

                    OutlinedIconButton(
                        modifier = Modifier
                            .weight(1f)
                            .size(IconButtonDefaults.mediumContainerSize()),
                        onClick = onShowLyrics,
                        shape = ShapeDefaults.Medium,
                        border = BorderStroke(1.5.dp, borderColor),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.lyrics), contentDescription = null, modifier = Modifier.size(IconButtonDefaults.mediumIconSize), tint = borderColor
                        )
                    }

                    OutlinedIconButton(
                        modifier = Modifier
                            .weight(1f)
                            .size(IconButtonDefaults.mediumContainerSize()),
                        onClick = playerConnection.player::toggleRepeatMode,
                        shape = ShapeDefaults.Medium,
                        border = BorderStroke(1.5.dp, if (repeatMode == Player.REPEAT_MODE_OFF) disableBorderColor else borderColor),
                    ) {
                        Icon(
                            painter = painterResource(
                                id = when (repeatMode) {
                                    Player.REPEAT_MODE_OFF, Player.REPEAT_MODE_ALL -> R.drawable.repeat
                                    Player.REPEAT_MODE_ONE -> R.drawable.repeat_one
                                    else -> R.drawable.repeat
                                }
                            ), contentDescription = null, modifier = Modifier.size(IconButtonDefaults.mediumIconSize), tint = if (repeatMode == Player.REPEAT_MODE_OFF) disableBorderColor else borderColor
                        )
                    }

                    OutlinedIconButton(
                        modifier = Modifier
                            .weight(1f)
                            .size(IconButtonDefaults.mediumContainerSize()), onClick = playerConnection::toggleLike, shape = ButtonGroupDefaults.connectedTrailingButtonShape, border = BorderStroke(1.5.dp, borderColor)
                    ) {
                        Icon(
                            painter = painterResource(id = if (currentSong?.song?.liked == true) R.drawable.favorite else R.drawable.favorite_border), contentDescription = null, modifier = Modifier.size(IconButtonDefaults.mediumIconSize), tint = borderColor
                        )
                    }
                }
            } else {
                // Old design
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 12.dp)
                        .windowInsetsPadding(
                            WindowInsets.systemBars.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal),
                        ),
                ) {
                    TextButton(
                        onClick = { state.expandSoft() }, modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.queue_music), contentDescription = null, modifier = Modifier.size(20.dp), tint = TextBackgroundColor
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(id = R.string.queue), color = TextBackgroundColor, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, modifier = Modifier.basicMarquee()
                            )
                        }
                    }

                    TextButton(
                        onClick = {
                            if (sleepTimerEnabled) {
                                playerConnection.service.sleepTimer.clear()
                            } else {
                                showSleepTimerDialog = true
                            }
                        }, modifier = Modifier.weight(1.2f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.bedtime), contentDescription = null, modifier = Modifier.size(20.dp), tint = TextBackgroundColor
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            AnimatedContent(
                                label = "sleepTimer",
                                targetState = sleepTimerEnabled,
                            ) { enabled ->
                                if (enabled) {
                                    Text(
                                        text = makeTimeString(sleepTimerTimeLeft), color = TextBackgroundColor, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, modifier = Modifier.basicMarquee()
                                    )
                                } else {
                                    Text(
                                        text = stringResource(id = R.string.sleep_timer), color = TextBackgroundColor, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, modifier = Modifier.basicMarquee()
                                    )
                                }
                            }
                        }
                    }

                    TextButton(
                        onClick = { onShowLyrics() }, modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.lyrics), contentDescription = null, modifier = Modifier.size(20.dp), tint = TextBackgroundColor
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(id = R.string.lyrics), color = TextBackgroundColor, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, modifier = Modifier.basicMarquee()
                            )
                        }
                    }
                }
            }

            if (showSleepTimerDialog) {
                ActionPromptDialog(titleBar = {
                    Row(
                        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.sleep_timer),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    }
                }, onDismiss = { showSleepTimerDialog = false }, onConfirm = {
                    showSleepTimerDialog = false
                    playerConnection.service.sleepTimer.start(sleepTimerValue.roundToInt())
                }, onCancel = {
                    showSleepTimerDialog = false
                }, onReset = {
                    sleepTimerValue = 30f // Default value
                }, content = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = pluralStringResource(
                                R.plurals.minute, sleepTimerValue.roundToInt(), sleepTimerValue.roundToInt()
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                        )

                        Spacer(Modifier.height(16.dp))

                        Slider(
                            value = sleepTimerValue, onValueChange = { sleepTimerValue = it }, valueRange = 5f..120f, steps = (120 - 5) / 5 - 1, modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                showSleepTimerDialog = false
                                playerConnection.service.sleepTimer.start(-1)
                            }) {
                            Text(stringResource(R.string.end_of_song))
                        }
                    }
                })
            }
        },
    ) {
        val queueTitle by playerConnection.queueTitle.collectAsState()
        val queueWindows by playerConnection.queueWindows.collectAsState()
        val automix by playerConnection.service.automixItems.collectAsState()
        val mutableQueueWindows = remember { mutableStateListOf<Timeline.Window>() }
        val queueLength = remember(queueWindows) {
            queueWindows.sumOf { it.mediaItem.metadata!!.duration }
        }

        val coroutineScope = rememberCoroutineScope()

        val headerItems = 1
        val lazyListState = rememberLazyListState()
        var dragInfo by remember { mutableStateOf<Pair<Int, Int>?>(null) }
        val shuffleModeEnabled by playerConnection.shuffleModeEnabled.collectAsState()

        val reorderableState = rememberReorderableLazyListState(
            lazyListState = lazyListState, scrollThresholdPadding = WindowInsets.systemBars.add(
                WindowInsets(
                    top = ListItemHeight, bottom = ListItemHeight
                )
            ).asPaddingValues()
        ) { from, to ->
            val currentDragInfo = dragInfo
            dragInfo = if (currentDragInfo == null) {
                from.index to to.index
            } else {
                currentDragInfo.first to to.index
            }

            val safeFrom = (from.index - headerItems).coerceIn(0, mutableQueueWindows.lastIndex)
            val safeTo = (to.index - headerItems).coerceIn(0, mutableQueueWindows.lastIndex)

            mutableQueueWindows.move(safeFrom, safeTo)
        }

        LaunchedEffect(reorderableState.isAnyItemDragging) {
            if (!reorderableState.isAnyItemDragging) {
                dragInfo?.let { (from, to) ->
                    val safeFrom = (from - headerItems).coerceIn(0, queueWindows.lastIndex)
                    val safeTo = (to - headerItems).coerceIn(0, queueWindows.lastIndex)

                    if (!playerConnection.player.shuffleModeEnabled) {
                        playerConnection.player.moveMediaItem(safeFrom, safeTo)
                    } else {
                        playerConnection.player.setShuffleOrder(
                            DefaultShuffleOrder(queueWindows.map { it.firstPeriodIndex }.toMutableList().move(safeFrom, safeTo).toIntArray(), System.currentTimeMillis())
                        )
                    }
                    dragInfo = null
                }
            }
        }

        LaunchedEffect(queueWindows) {
            mutableQueueWindows.apply {
                clear()
                addAll(queueWindows)
            }
        }

        LaunchedEffect(mutableQueueWindows) {
            if (currentWindowIndex != -1) {
                lazyListState.scrollToItem(currentWindowIndex, -16)
            }
        }
        Scaffold(
            topBar = {
                Column {
                    CenterAlignedTopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        title = {
                            Text(
                                text = queueTitle.orEmpty(),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = state::collapseSoft,
                            ) {
                                Icon(painterResource(R.drawable.close), null)
                            }
                        },
                        actions = {
                            AnimatedVisibility(
                                visible = !selection,
                                enter = fadeIn() + slideInVertically { it },
                                exit = fadeOut() + slideOutVertically { it },
                            ) {
                                IconButton(
                                    onClick = { locked = !locked },
                                ) {
                                    Icon(
                                        painter = painterResource(if (locked) R.drawable.lock else R.drawable.lock_open),
                                        contentDescription = null,
                                    )
                                }
                            }
                        }
                    )

                    AnimatedVisibility(
                        visible = selection,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically(),
                    ) {
                        Row(
                            modifier = Modifier.height(48.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            val count = selectedSongs.size
                            IconButton(
                                onClick = {
                                    selection = false
                                },
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.close),
                                    contentDescription = null,
                                )
                            }
                            Text(
                                text = stringResource(R.string.elements_selected, count), modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    if (count == mutableQueueWindows.size) {
                                        selectedSongs.clear()
                                        selectedItems.clear()
                                    } else {
                                        queueWindows.filter { it.mediaItem.metadata!! !in selectedSongs }.forEach {
                                            selectedSongs.add(it.mediaItem.metadata!!)
                                            selectedItems.add(it)
                                        }
                                    }
                                },
                            ) {
                                Icon(
                                    painter = painterResource(
                                        if (count == mutableQueueWindows.size) {
                                            R.drawable.deselect
                                        } else {
                                            R.drawable.select_all
                                        },
                                    ),
                                    contentDescription = null,
                                )
                            }

                            IconButton(
                                onClick = {
                                    menuState.show {
                                        SelectionMediaMetadataMenu(
                                            songSelection = selectedSongs,
                                            onDismiss = menuState::dismiss,
                                            clearAction = {
                                                selectedSongs.clear()
                                                selectedItems.clear()
                                            },
                                            currentItems = selectedItems,
                                        )
                                    }
                                },
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.more_vert),
                                    contentDescription = null,
                                    tint = LocalContentColor.current,
                                )
                            }
                        }
                    }
                }
            },
            floatingActionButton = {
                val containerColor = if (useDarkTheme || backgroundStyle == PlayerBackgroundStyle.DEFAULT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                val contentColor = if (useDarkTheme || backgroundStyle == PlayerBackgroundStyle.DEFAULT) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(containerColor),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                lazyListState.animateScrollToItem(
                                    if (playerConnection.player.shuffleModeEnabled) playerConnection.player.currentMediaItemIndex else 0,
                                )
                            }.invokeOnCompletion {
                                playerConnection.player.shuffleModeEnabled = !playerConnection.player.shuffleModeEnabled
                            }
                        },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.shuffle),
                            contentDescription = null,
                            modifier = Modifier.alpha(if (shuffleModeEnabled) 1f else 0.35f),
                            tint = contentColor
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = pluralStringResource(
                                R.plurals.n_song, queueWindows.size, queueWindows.size
                            ),
                            style = MaterialTheme.typography.bodyMedium.copy(color = contentColor),
                        )

                        Text(
                            text = makeTimeString(queueLength * 1000L),
                            style = MaterialTheme.typography.bodyMedium.copy(color = contentColor),
                        )
                    }

                    IconButton(
                        onClick = playerConnection.player::toggleRepeatMode,
                    ) {
                        Icon(
                            painter = painterResource(
                                when (repeatMode) {
                                    Player.REPEAT_MODE_OFF, Player.REPEAT_MODE_ALL -> R.drawable.repeat
                                    Player.REPEAT_MODE_ONE -> R.drawable.repeat_one
                                    else -> throw IllegalStateException()
                                },
                            ),
                            contentDescription = null,
                            modifier = Modifier.alpha(if (repeatMode == Player.REPEAT_MODE_OFF) 0.35f else 1f),
                            tint = contentColor,
                        )
                    }

                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
            ) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.nestedScroll(state.preUpPostDownNestedScrollConnection),
                    contentPadding = PaddingValues(bottom = 64.dp, top = 8.dp)
                ) {
                    itemsIndexed(
                        items = mutableQueueWindows,
                        key = { _, item -> item.uid.hashCode() },
                    ) { index, window ->
                        ReorderableItem(
                            state = reorderableState,
                            key = window.uid.hashCode(),
                        ) {
                            val currentItem by rememberUpdatedState(window)
                            val dismissBoxState = rememberSwipeToDismissBoxState(
                                positionalThreshold = { totalDistance -> totalDistance })

                            var processedDismiss by remember { mutableStateOf(false) }
                            LaunchedEffect(dismissBoxState.currentValue) {
                                val dv = dismissBoxState.currentValue
                                if (!processedDismiss && (dv == SwipeToDismissBoxValue.StartToEnd || dv == SwipeToDismissBoxValue.EndToStart)) {
                                    processedDismiss = true
                                    playerConnection.player.removeMediaItem(currentItem.firstPeriodIndex)
                                    dismissJob?.cancel()
                                    dismissJob = coroutineScope.launch {
                                        val snackbarResult = snackbarHostState.showSnackbar(
                                            message = context.getString(
                                                R.string.removed_song_from_playlist,
                                                currentItem.mediaItem.metadata?.title,
                                            ),
                                            actionLabel = context.getString(R.string.undo),
                                            duration = SnackbarDuration.Short,
                                        )
                                        if (snackbarResult == SnackbarResult.ActionPerformed) {
                                            playerConnection.player.addMediaItem(currentItem.mediaItem)
                                            playerConnection.player.moveMediaItem(
                                                mutableQueueWindows.size,
                                                currentItem.firstPeriodIndex,
                                            )
                                        }
                                    }
                                }
                                if (dv == SwipeToDismissBoxValue.Settled) {
                                    processedDismiss = false
                                }
                            }

                            val content: @Composable () -> Unit = {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    MediaMetadataListItem(
                                        mediaMetadata = window.mediaItem.metadata!!,
                                        isSelected = selection && window.mediaItem.metadata!! in selectedSongs,
                                        isActive = index == currentWindowIndex,
                                        isPlaying = isPlaying,
                                        trailingContent = {
                                            IconButton(
                                                onClick = {
                                                    menuState.show {
                                                        PlayerMenu(
                                                            mediaMetadata = window.mediaItem.metadata!!,
                                                            navController = navController,
                                                            playerBottomSheetState = playerBottomSheetState,
                                                            isQueueTrigger = true,
                                                            onShowDetailsDialog = {
                                                                window.mediaItem.mediaId.let {
                                                                    bottomSheetPageState.show {
                                                                        ShowMediaInfo(it)
                                                                    }
                                                                }
                                                            },
                                                            onDismiss = menuState::dismiss,
                                                        )
                                                    }
                                                },
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.more_vert),
                                                    contentDescription = null,
                                                )
                                            }
                                            if (!locked) {
                                                IconButton(
                                                    onClick = { }, modifier = Modifier.draggableHandle()
                                                ) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.drag_handle),
                                                        contentDescription = null,
                                                    )
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.surface)
                                            .combinedClickable(
                                                onClick = {
                                                    if (selection) {
                                                        if (window.mediaItem.metadata!! in selectedSongs) {
                                                            selectedSongs.remove(window.mediaItem.metadata!!)
                                                            selectedItems.remove(currentItem)
                                                        } else {
                                                            selectedSongs.add(window.mediaItem.metadata!!)
                                                            selectedItems.add(currentItem)
                                                        }
                                                    } else {
                                                        if (index == currentWindowIndex) {
                                                            playerConnection.player.togglePlayPause()
                                                        } else {
                                                            playerConnection.player.seekToDefaultPosition(
                                                                window.firstPeriodIndex,
                                                            )
                                                            playerConnection.player.playWhenReady = true
                                                        }
                                                    }
                                                },
                                                onLongClick = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    if (!selection) {
                                                        selection = true
                                                    }
                                                    selectedSongs.clear() // Clear all selections
                                                    selectedSongs.add(window.mediaItem.metadata!!) // Select current item
                                                },
                                            ),
                                    )
                                }
                            }

                            if (locked) {
                                content()
                            } else {
                                SwipeToDismissBox(
                                    state = dismissBoxState,
                                    backgroundContent = {},
                                ) {
                                    content()
                                }
                            }
                        }
                    }

                    if (automix.isNotEmpty()) {
                        item {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                            )

                            Text(
                                text = stringResource(R.string.similar_content),
                                modifier = Modifier.padding(start = 16.dp),
                            )
                        }

                        itemsIndexed(
                            items = automix,
                            key = { _, it -> it.mediaId },
                        ) { index, item ->
                            Row(
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                MediaMetadataListItem(
                                    mediaMetadata = item.metadata!!,
                                    trailingContent = {
                                        IconButton(
                                            onClick = {
                                                playerConnection.service.playNextAutomix(
                                                    item,
                                                    index,
                                                )
                                            },
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.playlist_play),
                                                contentDescription = null,
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                playerConnection.service.addToQueueAutomix(
                                                    item,
                                                    index,
                                                )
                                            },
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.queue_music),
                                                contentDescription = null,
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .combinedClickable(
                                            onClick = {},
                                            onLongClick = {
                                                menuState.show {
                                                    PlayerMenu(
                                                        mediaMetadata = item.metadata!!,
                                                        navController = navController,
                                                        playerBottomSheetState = playerBottomSheetState,
                                                        isQueueTrigger = true,
                                                        onShowDetailsDialog = {
                                                            item.mediaId.let {
                                                                bottomSheetPageState.show {
                                                                    ShowMediaInfo(it)
                                                                }
                                                            }
                                                        },
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                        )
                                        .animateItem(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
