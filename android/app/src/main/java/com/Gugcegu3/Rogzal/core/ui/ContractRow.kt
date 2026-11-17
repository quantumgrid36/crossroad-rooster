package com.Gugcegu3.Rogzal.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.Gugcegu3.Rogzal.R
import com.Gugcegu3.Rogzal.core.theme.BarnRed
import com.Gugcegu3.Rogzal.core.theme.FieldSage
import com.Gugcegu3.Rogzal.domain.model.ClauseKind
import com.Gugcegu3.Rogzal.domain.model.ContractClause

@Composable
fun clauseLabel(clause: ContractClause): String = when (clause.kind) {
    ClauseKind.DELIVER -> stringResource(R.string.clause_deliver, clause.target)
    ClauseKind.SIGNS -> stringResource(R.string.clause_signs, clause.target)
    ClauseKind.TICKS -> stringResource(R.string.clause_ticks, clause.target)
}

@Composable
fun ContractLine(
    clause: ContractClause,
    showResult: Boolean,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val accent = if (clause.met) FieldSage else BarnRed
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(
                when {
                    !showResult -> R.drawable.ic_clock
                    clause.met -> R.drawable.ic_check
                    else -> R.drawable.ic_cross
                }
            ),
            contentDescription = null,
            tint = if (showResult) accent else textColor.copy(alpha = 0.7f),
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = clauseLabel(clause),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (showResult) {
            Text(
                text = stringResource(R.string.clause_actual, clause.actual),
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ContractRow(
    clauses: List<ContractClause>,
    showResult: Boolean,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        clauses.forEach { clause ->
            ContractLine(clause = clause, showResult = showResult, textColor = textColor)
        }
    }
}
