package com.android.hwsystemmanager.compose

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.hwsystemmanager.utils.isLayoutRtl
import java.text.NumberFormat


@Composable
fun MultiColorLineChart(
    result: MutableList<StackBarPointData>,
    modifier: Modifier,
    chargeColor: Color = Color(0xFF64bb5c),
    noChargeColor: Color = Color(0xFF0a59f7),
    lowBatteryColor: Color = Color(0xFFFF5968),
    textStyle: TextStyle = TextStyle(color = Color(0xFF1F1F1F).copy(alpha = 0.25f), fontSize = 12.sp),
    dashLineColor: Color = Color(0xFFD8D8D8),
    lineWidth: Dp = 2.dp,
    dashLineWidth: Dp = 2.dp,
    lineGap: Dp = 40.dp,
    //电量百分比坐标与横向轴线的间隔
    percentCoorGap: Dp = 10.dp,
    bottomTextTopMargin: Dp = 48.dp
) {

    val textMeasurer = rememberTextMeasurer()
    val textLayout = textMeasurer.measure(AnnotatedString("现在"), textStyle)
    val percentTextLayout = textMeasurer.measure(AnnotatedString("100%"), textStyle)
    val pathRenderer = remember { MultiColorPathRenderer() }
    Canvas(modifier = modifier) {
        val chartHeight = this.size.height
        val chartWidth = this.size.width
        val chartStartY = lineGap.toPx()
        val chartStopY = chartHeight - bottomTextTopMargin.toPx() - textLayout.size.height
        val chartStartX = 0f
        val chartStopX = chartWidth - percentTextLayout.size.width - percentCoorGap.toPx()
        val barWidth = (chartStopX - chartStartX) / 48f
        drawChartLine(
            textMeasurer = textMeasurer,
            dashLineColor = dashLineColor,
            textStyle = textStyle,
            dashLineWidth = dashLineWidth,
            width = chartWidth,
            height = chartStopY - chartStartY,
            lineGap.toPx(),
            percentCoorGap.toPx(), 100
        )
        drawChartLine(
            textMeasurer = textMeasurer,
            dashLineColor = dashLineColor,
            textStyle = textStyle,
            dashLineWidth = dashLineWidth,
            width = chartWidth,
            height = chartStopY - chartStartY,
            lineGap.toPx(),
            percentCoorGap.toPx(), 50
        )
        drawChartLine(
            textMeasurer = textMeasurer,
            dashLineColor = dashLineColor,
            textStyle = textStyle,
            dashLineWidth = dashLineWidth,
            width = chartWidth,
            height = chartStopY - chartStartY,
            lineGap.toPx(),
            percentCoorGap.toPx(), 0
        )
        val pointFColors = mutableListOf<PointFColor>()
        for ((index, item) in result.withIndex()) {
            item.barWidth = (2f * barWidth) / 3f
            item.size = Size(width = barWidth, height = chartStopY - chartStartY)
            item.offset = Offset(x = index * barWidth, y = chartStartY)
            val preBar = result.getOrNull(index - 1) ?: result[0]
            val preLevel = preBar.level
            item.calculatePointF(preLevel, pointFColors)
            item.drawBar(this)
            if (index % 6 == 0) {
                drawBottomTime(
                    textMeasurer, textStyle = textStyle,
                    item, barWidth, chartHeight - textLayout.size.height,
                    index / 6
                )
            }
        }
        pathRenderer.setStrokeWidth(lineWidth.toPx())
        pathRenderer.setData(pointFColors)
        pathRenderer.draw(this)
        drawBottomTime(
            textMeasurer, textStyle = textStyle,
            result.last(), barWidth, chartHeight - textLayout.size.height,
            8
        )
    }
}

private fun DrawScope.drawBottomTime(
    textMeasurer: TextMeasurer,
    textStyle: TextStyle,
    data: StackBarPointData,
    barWidth: Float,
    y: Float,
    index: Int,
) {
    drawText(
        textMeasurer = textMeasurer,
        text = data.timeLabel,
        topLeft = Offset(index * barWidth * 6, y),
        style = textStyle,
        overflow = TextOverflow.Clip
    )
}

private fun DrawScope.drawChartLine(
    textMeasurer: TextMeasurer,
    dashLineColor: Color,
    textStyle: TextStyle,
    dashLineWidth: Dp,
    width: Float,
    height: Float,
    lineGap: Float,
    textGap: Float,
    value: Int,
) {
    val textLayout = textMeasurer.measure(AnnotatedString("100%"), style = textStyle)
    val textSize = textLayout.size
    val x = if (isLayoutRtl) width else width - textSize.width.toFloat()
    val y = (textSize.height / 2f) + ((lineGap + height) - ((value * height) / 100))
    val format = NumberFormat.getPercentInstance().format(value / 100.0)
    drawText(
        textMeasurer = textMeasurer,
        text = format,
        topLeft = Offset(x, y - textSize.height),
        style = textStyle,
        overflow = TextOverflow.Clip
    )
    val lineY = y - textSize.height / 2f
    drawLine(
        color = dashLineColor,
        start = Offset(0f, lineY),
        end = Offset(size.width - textSize.width - textGap, lineY),
        strokeWidth = dashLineWidth.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 6f), 0f)
    )

}