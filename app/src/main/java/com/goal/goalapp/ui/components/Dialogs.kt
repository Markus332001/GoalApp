package com.goal.goalapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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


@Preview
@Composable
fun DeleteDialogPreview() {
    DeleteDialog(
        onDismiss = {},
        onConfirm = {}
    )
}