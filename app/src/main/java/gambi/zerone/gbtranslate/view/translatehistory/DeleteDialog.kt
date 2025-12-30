package gambi.zerone.gbtranslate.view.translatehistory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gbtranslate.R

@Preview(showBackground = true)
@Composable
fun NotifyDialog(
    modifier: Modifier = Modifier,
    icon: Int = R.drawable.ic_delete,
    title: String = "Delete",
    description: String = "Are you sure you want to delete translate history",
    confirmText: String = "Delete",
    cancelText: String = "Cancel",
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray.copy(alpha = 0.25f))
            .clickable(
                indication = null,
                interactionSource = null,
                onClick = { onDismiss() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier
                .width(278.dp)
                .background(Color(0xFFF9F9F9), RoundedCornerShape(20.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = "Close Icon",
                tint = Color(0xFFBDBDBD),
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        onDismiss()
                    }
            )
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .background(Color(0xFF3162FF).copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = "Delete Icon",
                    tint = Color(0xFF3162FF),
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFF3162FF).copy(alpha = 0.2f), CircleShape)
                        .padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF9EA5AE)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onConfirm() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3162FF),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = confirmText, fontSize = 18.sp)
            }
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = cancelText, color = Color(0xFFCBCDD3), fontSize = 18.sp)
            }
        }
    }
}