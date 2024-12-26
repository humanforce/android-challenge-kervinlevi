package com.humanforce.humanforceandroidengineeringchallenge.presentation.location

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import com.humanforce.humanforceandroidengineeringchallenge.R
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location
import com.humanforce.humanforceandroidengineeringchallenge.presentation.common.SimpleToast
import com.humanforce.humanforceandroidengineeringchallenge.presentation.common.getDisplayText
import com.humanforce.humanforceandroidengineeringchallenge.presentation.location.LocationAction.SaveLocation
import com.humanforce.humanforceandroidengineeringchallenge.presentation.location.LocationAction.SearchLocation
import com.humanforce.humanforceandroidengineeringchallenge.presentation.location.LocationAction.UpdateLocation
import com.humanforce.humanforceandroidengineeringchallenge.presentation.location.LocationAction.UpdateQuery
import com.humanforce.humanforceandroidengineeringchallenge.presentation.location.LocationAction.UseUserLocation
import com.humanforce.humanforceandroidengineeringchallenge.presentation.location.LocationEvent.SaveFavoriteFailed
import com.humanforce.humanforceandroidengineeringchallenge.presentation.location.LocationEvent.SaveFavoriteSuccessful
import com.humanforce.humanforceandroidengineeringchallenge.presentation.main.BlueDarken3
import com.humanforce.humanforceandroidengineeringchallenge.presentation.main.BlueGreyLighten1
import com.humanforce.humanforceandroidengineeringchallenge.presentation.main.BlueGreyLighten4
import com.humanforce.humanforceandroidengineeringchallenge.presentation.main.DarkTextColor
import com.humanforce.humanforceandroidengineeringchallenge.presentation.main.Spacing
import com.humanforce.humanforceandroidengineeringchallenge.presentation.navigation.NavGraph

/**
 * Created by kervinlevi on 24/12/24
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(
    state: LocationState,
    favoriteLocations: List<Location>,
    onAction: (LocationAction) -> Unit,
    navigateTo: (String) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { LocationTopBar(navigateTo, scrollBehavior) }) { innerPadding ->
        Box(modifier = Modifier.background(BlueDarken3).imePadding()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(
                    top = innerPadding.calculateTopPadding().plus(Spacing.large),
                    bottom = innerPadding.calculateBottomPadding(),
                    start = Spacing.large,
                    end = Spacing.large
                )
            ) {
                item { SearchOption(state, onAction) }
                items(state.searchResults) {
                    SearchResultItem(it, onAction, navigateTo)
                }
                item { RoundedFooter() }

                item {
                    UserLocationOption(onAction, navigateTo)
                }

                if (favoriteLocations.isNotEmpty()) {
                    item { FavoriteOptionHeader() }
                    items(favoriteLocations) {
                        FavoriteLocationItem(it, onAction, navigateTo)
                    }
                    item { RoundedFooter() }
                }
            }
        }

        when(state.oneTimeEvent?.consumeOneTimeEvent()) {
            SaveFavoriteSuccessful -> SimpleToast(R.string.added_favorites)
            SaveFavoriteFailed -> SimpleToast(R.string.failed_add_favorites)
            null -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationTopBar(navigateTo: (String) -> Unit, scrollBehavior: TopAppBarScrollBehavior) {
    MediumTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.select_location),
                maxLines = 1,
                style = MaterialTheme.typography.headlineSmall,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = Spacing.normal)
            )
        },

        colors = TopAppBarDefaults.topAppBarColors().copy(
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            containerColor = BlueDarken3,
            scrolledContainerColor = BlueDarken3
        ), navigationIcon = {
            IconButton(
                onClick = { navigateTo(NavGraph.WEATHER_REPORT) },
                modifier = Modifier.padding(start = Spacing.small)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        }, scrollBehavior = scrollBehavior
    )
}

@Composable
fun SearchOption(state: LocationState, onAction: (LocationAction) -> Unit) {
    TextField(
        modifier = Modifier.fillMaxWidth().padding(),
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            errorContainerColor = Color.White,
            disabledContainerColor = Color.White,
            unfocusedIndicatorColor = BlueGreyLighten4,
            focusedIndicatorColor = BlueGreyLighten4
        ),
        textStyle = MaterialTheme.typography.titleSmall.copy(color = DarkTextColor),
        maxLines = 1,
        shape = RoundedCornerShape(
            topStart = Spacing.normal, topEnd = Spacing.normal
        ),
        value = state.query,
        onValueChange = { onAction(UpdateQuery(it)) },
        placeholder = {
            Text(text = stringResource(R.string.search_location))
        },
        trailingIcon = {
            if (state.isSearchLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.width(Spacing.medium).height(Spacing.medium)
                )
            } else {
                IconButton(onClick = { onAction(SearchLocation) }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search_location),
                        tint = BlueGreyLighten1
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(onSearch = {
            onAction(SearchLocation)
        })
    )

    when (state.searchError) {
        is LocationError.EmptyQuery -> stringResource(R.string.error_empty_query)
        is LocationError.EmptyResult -> stringResource(R.string.error_empty_result)
        is LocationError.NoInternet -> stringResource(R.string.check_internet_connection)
        is LocationError.HttpError -> stringResource(
            R.string.generic_error, state.searchError.message
        )

        else -> null
    }?.let { errorText ->
        Box(modifier = Modifier.background(color = Color.White)) {
            Text(
                text = errorText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = Spacing.medium, vertical = Spacing.small)
            )
        }
    }
}

@Composable
fun SearchResultItem(
    item: Location, onAction: (LocationAction) -> Unit, navigateTo: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().background(Color.White).clickable(true) {
        onAction(UpdateLocation(item))
        navigateTo(NavGraph.WEATHER_REPORT)
    }) {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.getDisplayText(),
                style = MaterialTheme.typography.titleSmall,
                color = DarkTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(
                    horizontal = Spacing.medium, vertical = Spacing.large
                )
            )
            IconButton(onClick = { onAction(SaveLocation(item)) }) {
                Icon(
                    imageVector = Icons.Filled.FavoriteBorder,
                    contentDescription = stringResource(R.string.add_favorite),
                    tint = BlueGreyLighten1,
                    modifier = Modifier.size(Spacing.medium)
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(), color = BlueGreyLighten4
        )
    }
}

@Composable
fun UserLocationOption(onAction: (LocationAction) -> Unit, navigateTo: (String) -> Unit) {
    Card(colors = CardDefaults.cardColors().copy(containerColor = Color.White),
        shape = RoundedCornerShape(size = Spacing.normal),
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.large).clickable(true) {
                onAction(UseUserLocation)
                navigateTo(NavGraph.WEATHER_REPORT)
            }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(Spacing.normal))
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = stringResource(R.string.use_current_location),
                tint = BlueGreyLighten1
            )
            Spacer(modifier = Modifier.width(Spacing.small))
            Text(
                text = stringResource(R.string.use_current_location),
                style = MaterialTheme.typography.titleSmall,
                color = DarkTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.large).weight(1f)
            )
        }
    }
}

@Composable
fun FavoriteOptionHeader() {
    Card(
        colors = CardDefaults.cardColors().copy(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = Spacing.normal, topEnd = Spacing.normal
        )
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(Spacing.normal))
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = stringResource(R.string.select_favorite),
                tint = BlueGreyLighten1
            )
            Spacer(modifier = Modifier.width(Spacing.small))
            Text(
                text = stringResource(R.string.select_favorite),
                style = MaterialTheme.typography.titleSmall,
                color = DarkTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.large).weight(1f)
            )
        }
    }
}

@Composable
fun FavoriteLocationItem(
    item: Location, onAction: (LocationAction) -> Unit, navigateTo: (String) -> Unit
) {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(), color = BlueGreyLighten4
    )
    Box(modifier = Modifier.fillMaxWidth().background(Color.White).clickable(true) {
            onAction(UpdateLocation(item))
            navigateTo(NavGraph.WEATHER_REPORT)
        }) {
        Text(
            text = item.getDisplayText(),
            style = MaterialTheme.typography.titleSmall,
            color = DarkTextColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth().padding(
                horizontal = Spacing.medium, vertical = Spacing.large
            )
        )
    }
}

@Composable
fun RoundedFooter() {
    Card(colors = CardDefaults.cardColors().copy(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().height(Spacing.normal),
        shape = RoundedCornerShape(
            bottomStart = Spacing.normal, bottomEnd = Spacing.normal
        ),
        content = {})
}
