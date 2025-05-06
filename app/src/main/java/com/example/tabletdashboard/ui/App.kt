package com.example.tabletdashboard.ui

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ModeNight
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tabletdashboard.ui.theme.TabletDashboardTheme
import com.example.tabletdashboard.ui.widgets.ButtonArrayWidget
import com.example.tabletdashboard.ui.widgets.CalendarMonthWidget
import com.example.tabletdashboard.ui.widgets.CalendarTimelineWidget
import com.example.tabletdashboard.ui.widgets.ClockWidget
import com.example.tabletdashboard.ui.widgets.MVGDeparturesWidget
import com.example.tabletdashboard.ui.widgets.MensaMenuWidget
import com.example.tabletdashboard.ui.widgets.ObsidianDailyWidgetWithPermissionCheck
import com.example.tabletdashboard.ui.widgets.PictureWidget
import com.example.tabletdashboard.ui.widgets.PomodoroTimerWidget
import com.example.tabletdashboard.ui.widgets.RequestCalendarPermission
import com.example.tabletdashboard.ui.widgets.ShoppingListWidget
import com.example.tabletdashboard.ui.widgets.WeatherWidget
import com.example.tabletdashboard.ui.widgets.Widget
import com.example.tabletdashboard.viewmodels.AppViewModel

@Composable
fun App(isDarkTheme: Boolean) {

    val page1 = DashboardPage(
        rows = 4,
        cols = 7,
        items = listOf(
            GridPosition(row = 0, column = 0, rowSpan = 1, colSpan = 1, widget = Widget.CLOCK),
            GridPosition(row = 2, column = 5, rowSpan = 2, colSpan = 2, widget = Widget.CALENDAR_TIMELINE),
            GridPosition(row = 3, column = 1, rowSpan = 1, colSpan = 2, widget = Widget.MVG_DEPARTURES),
            GridPosition(row = 0, column = 5, rowSpan = 2, colSpan = 2, widget = Widget.MENSA_ARCISSTRASSE),
            GridPosition(row = 1, column = 0, rowSpan = 2, colSpan = 2, widget = Widget.CALENDAR_MONTH),
            GridPosition(row = 3, column = 0, rowSpan = 1, colSpan = 1, widget = Widget.POMODORO_TIMER),
            GridPosition(row = 0, column = 2, rowSpan = 3, colSpan = 3, widget = Widget.OBSIDIAN_VIEW),
            GridPosition(row = 0, column = 1, rowSpan = 1, colSpan = 1, widget = Widget.WEATHER),
        )
    )

    val page2 = DashboardPage(
        rows = 4,
        cols = 7,
        items = listOf(
            GridPosition(row = 0, column = 0, rowSpan = 1, colSpan = 2, ),
            GridPosition(row = 0, column = 2, rowSpan = 3, colSpan = 3, ),
            GridPosition(row = 0, column = 5, rowSpan = 1, colSpan = 2, ),
            GridPosition(row = 1, column = 5, rowSpan = 3, colSpan = 2, widget = Widget.SHOPPING_LIST),
            GridPosition(row = 1, column = 0, rowSpan = 2, colSpan = 2, ),
            GridPosition(row = 3, column = 0, rowSpan = 1, colSpan = 1, ),
            GridPosition(row = 3, column = 1, rowSpan = 1, colSpan = 2, ),
            GridPosition(row = 3, column = 3, rowSpan = 1, colSpan = 1, ),
            GridPosition(row = 3, column = 4, rowSpan = 1, colSpan = 1, ),
        )
    )

    val pages = mutableListOf<DashboardPage>()

    pages.add(page1)
    pages.add(page2)

    TabletDashboardTheme(darkTheme = isDarkTheme) {
        val pagerState = rememberPagerState(pageCount = { pages.size }, initialPage = 0)

        KeepScreenOn()
        HorizontalPager(state = pagerState) { page ->
            HomeGridLayout(
                modifier = Modifier.fillMaxSize(),
                items = pages[page].items,
                rows = pages[page].rows, cols = pages[page].cols
            )

        }
//        HomeGridLayout(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(color = if (isDarkTheme) Color.White else Color.Black),
//            items = items,
//            rows = rows, cols = cols
//        )
    }
}

@Composable
fun KeepScreenOn() {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}

@Composable
fun HomeGridLayout(
    modifier: Modifier = Modifier,
    items: List<GridPosition>,
    rows: Int = 9,
    cols: Int = 16,
) {
    BoxWithConstraints(modifier.padding(32.dp, 48.dp)) {
        val cellWidth = this.maxWidth / cols
        val cellHeight = this.maxHeight / rows

        Box(modifier = Modifier.fillMaxSize()) {
            items.forEach { position ->
                Box(
                    modifier = Modifier
                        .offset(
                            x = cellWidth * position.column,
                            y = cellHeight * position.row
                        )
                        .size(
                            width = position.colSpan * cellWidth,
                            height = position.rowSpan * cellHeight
                        )
                        .padding(8.dp)
                ) {
                    when (position.widget) {
                        Widget.CLOCK -> ClockWidget(
                            modifier = Modifier.fillMaxSize()
                        )
                        Widget.CALENDAR_MONTH -> CalendarMonthWidget(
                            modifier = Modifier.fillMaxSize(),
                            rowSpan = position.rowSpan,
                            colSpan = position.colSpan
                        )
                        Widget.CALENDAR_TIMELINE -> RequestCalendarPermission {
                            CalendarTimelineWidget(Modifier.fillMaxSize(), LocalContext.current)
                        }
                        Widget.WEATHER -> WeatherWidget()
                        Widget.MVG_DEPARTURES -> MVGDeparturesWidget()
                        Widget.MENSA_ARCISSTRASSE -> MensaMenuWidget()
                        Widget.POMODORO_TIMER -> PomodoroTimerWidget()
                        Widget.BUTTON_ARRAY -> {
                            val appViewModel = viewModel<AppViewModel>()
                            ButtonArrayWidget(
                                topLeft = { modifier ->
                                    IconButton(modifier = modifier.background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)), onClick = {appViewModel.isDarkMode.value = !appViewModel.isDarkMode.value}) {
                                        Icon(
                                            if (appViewModel.isDarkMode.value) Icons.Outlined.WbSunny else Icons.Outlined.ModeNight,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            contentDescription = "toggle dark mode",
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                },
                                topRight =  { modifier -> Box(modifier = modifier.background(color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(8.dp))) },
                                bottomLeft =  { modifier -> Box(modifier = modifier.background(color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(8.dp))) },
                                bottomRight =  { modifier -> Box(modifier = modifier.background(color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(8.dp))) }
                            )
                        }
                        Widget.PICTURE_WIDGET -> PictureWidget()
                        Widget.SHOPPING_LIST -> ShoppingListWidget()
                        Widget.OBSIDIAN_VIEW -> ObsidianDailyWidgetWithPermissionCheck()
                        else -> WidgetPlaceholder("${position.rowSpan} * ${position.colSpan}")
                    }
                }
            }
        }
    }
}

@Composable
fun WidgetPlaceholder(label: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onPrimary)
    }
}


data class GridPosition(
    val row: Int,
    val column: Int,
    val rowSpan: Int = 1,
    val colSpan: Int = 1,
    val widget: Widget = Widget.PLACEHOLDER
)

data class DashboardPage(
    val rows: Int,
    val cols: Int,
    val items: List<GridPosition>
)
