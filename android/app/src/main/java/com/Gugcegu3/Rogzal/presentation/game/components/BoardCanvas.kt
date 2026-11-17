package com.Gugcegu3.Rogzal.presentation.game.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Gugcegu3.Rogzal.R
import com.Gugcegu3.Rogzal.core.theme.Asphalt
import com.Gugcegu3.Rogzal.core.theme.BarkBrown
import com.Gugcegu3.Rogzal.core.theme.BarnRed
import com.Gugcegu3.Rogzal.core.theme.CreamLight
import com.Gugcegu3.Rogzal.core.theme.CreamPaint
import com.Gugcegu3.Rogzal.core.theme.DuskDeep
import com.Gugcegu3.Rogzal.core.theme.DuskMid
import com.Gugcegu3.Rogzal.core.theme.DuskShadow
import com.Gugcegu3.Rogzal.core.theme.FieldSage
import com.Gugcegu3.Rogzal.core.theme.FieldSageDeep
import com.Gugcegu3.Rogzal.domain.model.Cell
import com.Gugcegu3.Rogzal.domain.model.Move
import com.Gugcegu3.Rogzal.domain.model.RouteNode
import com.Gugcegu3.Rogzal.domain.model.RowKind
import com.Gugcegu3.Rogzal.domain.model.SignState
import com.Gugcegu3.Rogzal.domain.model.VehicleView

@Composable
fun BoardCanvas(
    rows: List<RowKind>,
    cols: Int,
    cell: Dp,
    frame: Dp,
    rooster: Cell,
    vehicles: List<VehicleView>,
    parcels: List<Cell>,
    barn: Cell,
    shadowCells: List<Cell>,
    signs: List<SignState>,
    route: List<RouteNode>,
    start: Cell,
    carrying: Boolean,
    highContrast: Boolean,
    collisionCell: Cell?,
    stepMs: Int,
    animated: Boolean,
    showBadges: Boolean,
    modifier: Modifier = Modifier,
    onCellTap: ((Cell) -> Unit)? = null
) {
    val boardWidth = cell * cols + frame * 2
    val boardHeight = cell * rows.size + frame * 2
    val shake by animateFloatAsState(
        targetValue = if (collisionCell != null) 1f else 0f,
        animationSpec = tween(260),
        label = "boardShake"
    )
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = BarkBrown,
        border = BorderStroke(2.dp, DuskMid),
        shadowElevation = 12.dp,
        modifier = modifier
            .size(width = boardWidth, height = boardHeight)
            .graphicsLayer { translationX = shake * 10f }
    ) {
        Box(modifier = Modifier.padding(frame)) {
            val density = LocalDensity.current
            val cellPx = with(density) { cell.toPx() }
            val tapHandler = onCellTap
            val gridModifier = if (tapHandler == null) {
                Modifier
            } else {
                Modifier.pointerInput(cellPx, cols, rows.size) {
                    detectTapGestures { position ->
                        val col = (position.x / cellPx).toInt().coerceIn(0, cols - 1)
                        val row = (position.y / cellPx).toInt().coerceIn(0, rows.size - 1)
                        tapHandler(Cell(row, col))
                    }
                }
            }
            Box(
                modifier = Modifier
                    .size(width = cell * cols, height = cell * rows.size)
                    .clip(RoundedCornerShape(6.dp))
                    .then(gridModifier)
            ) {
                BoardSurface(
                    rows = rows,
                    cols = cols,
                    cellPx = cellPx,
                    shadowCells = shadowCells,
                    highContrast = highContrast,
                    collisionCell = collisionCell,
                    flash = shake
                )
                RouteOverlay(
                    route = route,
                    start = start,
                    cellPx = cellPx
                )

                CellImage(
                    resId = R.drawable.sprite_5,
                    description = stringResource(R.string.cd_barn),
                    cell = cell,
                    position = barn,
                    inset = 4.dp
                )
                parcels.forEach { parcel ->
                    key(parcel.row, parcel.col) {
                        CellImage(
                            resId = R.drawable.sprite_4,
                            description = stringResource(R.string.cd_parcel),
                            cell = cell,
                            position = parcel,
                            inset = 10.dp
                        )
                    }
                }
                signs.forEach { sign ->
                    key(sign.laneIndex) {
                        SignMarker(cell = cell, sign = sign)
                    }
                }
                vehicles.forEach { vehicle ->
                    key(vehicle.laneIndex, vehicle.id) {
                        VehiclePiece(
                            vehicle = vehicle,
                            cell = cell,
                            stepMs = stepMs,
                            animated = animated
                        )
                    }
                }
                RoosterPiece(
                    rooster = rooster,
                    cell = cell,
                    carrying = carrying,
                    stepMs = stepMs,
                    animated = animated
                )
                if (showBadges) {
                    RouteBadges(route = route, cell = cell)
                }
            }
        }
    }
}

@Composable
private fun BoardSurface(
    rows: List<RowKind>,
    cols: Int,
    cellPx: Float,
    shadowCells: List<Cell>,
    highContrast: Boolean,
    collisionCell: Cell?,
    flash: Float
) {
    val hit = collisionCell
    val dashColor = if (highContrast) Color.White else CreamPaint
    val dashWidth = if (highContrast) 4f else 3f
    Canvas(modifier = Modifier.fillMaxSize()) {
        val unit = cellPx
        rows.forEachIndexed { rowIndex, kind ->
            for (col in 0 until cols) {
                val topLeft = Offset(col * unit, rowIndex * unit)
                val cellSize = Size(unit, unit)
                if (kind == RowKind.VERGE) {
                    drawRect(FieldSage, topLeft, cellSize)
                    drawRect(
                        color = FieldSageDeep.copy(alpha = 0.55f),
                        topLeft = Offset(topLeft.x + 3f, topLeft.y + 3f),
                        size = Size(unit - 6f, unit - 6f),
                        style = Stroke(width = 1.5f)
                    )
                } else {
                    drawRect(Asphalt, topLeft, cellSize)
                }
                if (shadowCells.any { it.row == rowIndex && it.col == col }) {
                    drawRect(DuskDeep.copy(alpha = 0.45f), topLeft, cellSize)
                }
                drawRect(
                    color = DuskShadow.copy(alpha = 0.18f),
                    topLeft = topLeft,
                    size = cellSize,
                    style = Stroke(width = 1f)
                )
            }
            if (kind == RowKind.ROAD) {
                val centerY = rowIndex * unit + unit / 2f
                drawLine(
                    color = dashColor.copy(alpha = 0.85f),
                    start = Offset(0f, centerY),
                    end = Offset(cols * unit, centerY),
                    strokeWidth = dashWidth,
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(unit * 0.26f, unit * 0.2f),
                        0f
                    )
                )
            }
        }
        if (hit != null) {
            drawRect(
                color = BarnRed.copy(alpha = 0.3f + 0.45f * flash),
                topLeft = Offset(hit.col * unit, hit.row * unit),
                size = Size(unit, unit)
            )
        }
    }
}

@Composable
private fun RouteOverlay(route: List<RouteNode>, start: Cell, cellPx: Float) {
    if (route.isEmpty()) return
    Canvas(modifier = Modifier.fillMaxSize()) {
        val unit = cellPx
        var previous = start
        route.forEach { node ->
            val from = Offset(previous.col * unit + unit / 2f, previous.row * unit + unit / 2f)
            val to = Offset(node.cell.col * unit + unit / 2f, node.cell.row * unit + unit / 2f)
            if (node.move == Move.WAIT) {
                drawCircle(
                    color = CreamPaint.copy(alpha = 0.85f),
                    radius = unit * 0.22f,
                    center = to,
                    style = Stroke(width = 3f)
                )
            } else {
                drawLine(
                    color = CreamPaint.copy(alpha = 0.85f),
                    start = from,
                    end = to,
                    strokeWidth = 5f,
                    cap = StrokeCap.Round
                )
                drawCircle(
                    color = CreamPaint.copy(alpha = 0.9f),
                    radius = unit * 0.1f,
                    center = to
                )
            }
            previous = node.cell
        }
    }
}

@Composable
private fun RouteBadges(route: List<RouteNode>, cell: Dp) {
    route.take(MAX_BADGES).forEachIndexed { index, node ->
        key(index, node.cell.row, node.cell.col) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .offset(x = cell * node.cell.col, y = cell * node.cell.row)
                    .size(cell)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(DuskShadow.copy(alpha = 0.72f))
                ) {
                    Text(
                        text = (index + 1).toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = CreamLight
                    )
                }
            }
        }
    }
}

@Composable
private fun CellImage(
    resId: Int,
    description: String?,
    cell: Dp,
    position: Cell,
    inset: Dp
) {
    Image(
        painter = painterResource(resId),
        contentDescription = description,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .offset(x = cell * position.col, y = cell * position.row)
            .size(cell)
            .padding(inset)
    )
}

@Composable
private fun SignMarker(cell: Dp, sign: SignState) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .offset(x = cell * sign.col, y = cell * sign.row)
            .size(cell)
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(width = 3.dp, height = cell / 3)
                .background(CreamPaint)
        )
        Image(
            painter = painterResource(R.drawable.ic_sign),
            contentDescription = stringResource(R.string.cd_sign),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(cell * 0.5f)
        )
    }
}

@Composable
private fun VehiclePiece(
    vehicle: VehicleView,
    cell: Dp,
    stepMs: Int,
    animated: Boolean
) {
    val spec: FiniteAnimationSpec<Dp> = if (animated) {
        tween(durationMillis = stepMs.coerceAtLeast(80), easing = FastOutSlowInEasing)
    } else {
        snap()
    }
    val x by animateDpAsState(
        targetValue = cell * vehicle.col,
        animationSpec = spec,
        label = "vehicleX"
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .offset(x = x, y = cell * vehicle.row)
            .size(cell)
            .padding(3.dp)
    ) {
        if (vehicle.frozen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(CreamPaint.copy(alpha = 0.35f))
            )
        }
        Image(
            painter = painterResource(
                if (vehicle.heavy) R.drawable.sprite_3 else R.drawable.sprite_2
            ),
            contentDescription = stringResource(
                if (vehicle.heavy) R.string.cd_truck else R.string.cd_tractor
            ),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { scaleX = if (vehicle.facingRight) 1f else -1f }
        )
    }
}

@Composable
private fun RoosterPiece(
    rooster: Cell,
    cell: Dp,
    carrying: Boolean,
    stepMs: Int,
    animated: Boolean
) {
    val spec: FiniteAnimationSpec<Dp> = if (animated) {
        tween(durationMillis = stepMs.coerceAtLeast(80), easing = FastOutSlowInEasing)
    } else {
        snap()
    }
    val x by animateDpAsState(
        targetValue = cell * rooster.col,
        animationSpec = spec,
        label = "roosterX"
    )
    val y by animateDpAsState(
        targetValue = cell * rooster.row,
        animationSpec = spec,
        label = "roosterY"
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .offset(x = x, y = y)
            .size(cell)
            .padding(4.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.sprite_1),
            contentDescription = stringResource(R.string.cd_rooster),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationZ = if (carrying) -8f else 0f }
        )
        if (carrying) {
            Image(
                painter = painterResource(R.drawable.sprite_4),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(cell * 0.32f)
            )
        }
    }
}

private const val MAX_BADGES = 24
