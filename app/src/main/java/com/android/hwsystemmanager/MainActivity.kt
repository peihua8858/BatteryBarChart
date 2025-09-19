package com.android.hwsystemmanager

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.hwsystemmanager.compose.AutoLineHeightScaleText
import com.android.hwsystemmanager.compose.ChartViewModel
import com.android.hwsystemmanager.compose.MultiColorLineChart
import com.android.hwsystemmanager.compose.MultiStateScreen
import com.android.hwsystemmanager.ui.theme.HwSystemManagerTheme
import com.android.hwsystemmanager.utils.isLandscape
import androidx.core.graphics.drawable.toDrawable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        window.setBackgroundDrawable(android.graphics.Color.GRAY.toDrawable())
        setContent {
            HwSystemManagerTheme {
                val isLandscape = isLandscape
                val viewModel = viewModel<ChartViewModel>()
                MultiStateScreen(
                    modifier = Modifier.background(Color.Gray),
                    title = stringResource(id = R.string.battery_level),
                    result = viewModel.timeState.value,
                    refresh = viewModel::requestData,
                    navigateUp = { finish() },
                ) {
                    Column(
                        modifier = Modifier
                            .background(Color.White)
                            .padding(start = 16.dp, end = 16.dp)
                    ) {
                        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                            val (title, imgCharge, charge, imgNoCharge, noCharge, imgLowBattery, lowBattery) = createRefs()
                            Text(
                                modifier = Modifier.constrainAs(title) {
                                    top.linkTo(parent.top)
                                },
                                text = stringResource(id = R.string.battery_level)
                            )

                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(Color.Green, RoundedCornerShape(4.dp))
                                    .constrainAs(imgCharge) {
                                        top.linkTo(charge.top)
                                        bottom.linkTo(charge.bottom)
                                        if (isLandscape) {
                                            end.linkTo(charge.start)
                                        }
                                    },
                            )
                            Text(
                                modifier = Modifier.constrainAs(charge) {
                                    if (isLandscape) {
                                        top.linkTo(parent.top)
                                        end.linkTo(imgNoCharge.start, 24.dp)
                                    } else {
                                        top.linkTo(title.bottom, 18.dp)
                                        start.linkTo(imgCharge.end)
                                    }
                                },
                                text = stringResource(id = R.string.charge_time)
                            )
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(Color.Blue, RoundedCornerShape(4.dp))
                                    .constrainAs(imgNoCharge) {
                                        top.linkTo(noCharge.top)
                                        bottom.linkTo(noCharge.bottom)
                                        if (isLandscape) {
                                            end.linkTo(noCharge.start)
                                        } else {
                                            start.linkTo(charge.end, 13.dp)
                                        }
                                    },
                            )
                            Text(
                                modifier = Modifier
                                    .constrainAs(noCharge) {
                                        if (isLandscape) {
                                            top.linkTo(parent.top)
                                            end.linkTo(imgLowBattery.start, 24.dp)
                                        } else {
                                            top.linkTo(title.bottom, 18.dp)
                                            start.linkTo(imgNoCharge.end)
                                        }
                                    },
                                text = stringResource(id = R.string.consumption_time)
                            )
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(Color.Red, RoundedCornerShape(4.dp))
                                    .constrainAs(imgLowBattery) {
                                        top.linkTo(lowBattery.top)
                                        bottom.linkTo(lowBattery.bottom)
                                        if (isLandscape) {
                                            end.linkTo(lowBattery.start)
                                        } else {
                                            start.linkTo(noCharge.end, 13.dp)
                                        }

                                    },
                            )
                            AutoLineHeightScaleText(
                                modifier = Modifier.constrainAs(lowBattery) {
                                    if (isLandscape) {
                                        top.linkTo(parent.top)
                                        end.linkTo(parent.end, 13.dp)
                                    } else {
                                        top.linkTo(title.bottom, 18.dp)
                                        start.linkTo(imgLowBattery.end)
                                    }

                                },
                                text = stringResource(id = R.string.low_battery_time)
                            )
                        }
                        MultiColorLineChart(
                            result = it,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(244.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HwSystemManagerTheme {
        Greeting("Android")
    }
}
