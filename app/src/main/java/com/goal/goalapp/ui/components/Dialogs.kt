package com.goal.goalapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.goal.goalapp.R
import com.goal.goalapp.data.group.GroupCategory

@Composable
fun DeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
){
    Dialog(
        onDismissRequest = { onDismiss() },
    ){
        Box(
            modifier = modifier
                .shadow(8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .wrapContentSize()
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ){
                Text(
                    text = stringResource(R.string.delete_question),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.cardsBackground),
                            contentColor = colorResource(R.color.button_font)
                        ),
                        modifier = Modifier
                            .padding(5.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .fillMaxWidth()
                            .weight(1f)
                    ){
                        Text( text = stringResource(R.string.cancel))
                    }
                    Button(
                        onClick = {
                            onConfirm()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.negative),
                            contentColor = colorResource(R.color.button_font_light)
                        ),
                        modifier = Modifier
                            .padding(5.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .fillMaxWidth()
                            .weight(1f)
                    ){
                        Text(text = stringResource(R.string.delete))
                    }
                }
            }
        }
    }
}

@Composable
fun SelectCategoriesDialog(
    searchLabel: String,
    allCategories: List<GroupCategory>,
    selectedCategories: List<GroupCategory>,
    onDismiss: () -> Unit,
    onConfirm: (List<GroupCategory>) -> Unit,
    modifier: Modifier
){
    val searchInput = remember { mutableStateOf("") }
    val newSelectedCategories = remember { selectedCategories.toMutableList() }
    val searchedCategories = remember { mutableStateOf(allCategories) }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = modifier,
        ){
            SearchBar(
                searchInput = searchInput.value,
                label = searchLabel,
                onSearchInputChanged = {
                    searchInput.value = it
                    searchedCategories.value = getSearchCategories(searchInput.value, allCategories)
                },
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(modifier.padding(top = 5.dp, bottom = 5.dp).fillMaxWidth())

            LazyColumn {
                items(searchedCategories.value.size) { index ->
                    val category = searchedCategories.value[index]
                    val isSelected = newSelectedCategories.contains(category)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = {
                                if(isSelected){
                                    newSelectedCategories.remove(category)
                                } else {
                                    newSelectedCategories.add(category)
                                }
                            },
                            modifier = Modifier.padding(end = 10.dp)
                        )
                    }
                }
            }

            HorizontalDivider(modifier.padding(top = 5.dp, bottom = 5.dp).fillMaxWidth())

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Button(
                    onClick = { newSelectedCategories.clear() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.cardsBackground),
                        contentColor = colorResource(R.color.button_font)
                    ),
                    modifier = Modifier
                        .padding(5.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .fillMaxWidth()
                        .weight(1f)
                ){
                    Text( text = stringResource(R.string.reset))
                }
                Spacer(modifier = Modifier.weight(0.4f))

                Button(
                    onClick = { onConfirm(newSelectedCategories) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.primary),
                        contentColor = colorResource(R.color.button_font_light)
                    ),
                    modifier = Modifier
                        .padding(5.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .fillMaxWidth()
                        .weight(1f)
                ){
                    Text( text = stringResource(R.string.confirm))
                }

                Button(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.cardsBackground),
                        contentColor = colorResource(R.color.button_font)
                    ),
                    modifier = Modifier
                        .padding(5.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .fillMaxWidth()
                        .weight(1f)
                ){
                    Text( text = stringResource(R.string.cancel))
                }
            }
        }
    }
}

fun getSearchCategories(
    searchInput: String,
    allCategories: List<GroupCategory>
): List<GroupCategory> {
    return allCategories.filter { category ->
        category.name.contains(searchInput, ignoreCase = true)
    }
}


@Preview
@Composable
fun DeleteDialogPreview() {
    DeleteDialog(
        onDismiss = {},
        onConfirm = {}
    )
}