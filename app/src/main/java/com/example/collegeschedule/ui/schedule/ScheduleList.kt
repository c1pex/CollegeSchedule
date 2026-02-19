package com.example.collegeschedule.ui.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.dto.ScheduleByDateDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ScheduleList(data: List<ScheduleByDateDto>) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {

        items(data) { day ->

            val parsedDate = try {
                LocalDate.parse(day.lessonDate.substring(0, 10))
            } catch (e: Exception) {
                null
            }

            val formattedDate = parsedDate?.format(
                DateTimeFormatter.ofPattern("dd.MM.yyyy")
            ) ?: day.lessonDate

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = day.weekday,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (day.lessons.isEmpty()) {

                Text(
                    "Информация отсутствует",
                    modifier = Modifier.padding(start = 8.dp, bottom = 12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )

            } else {

                day.lessons.forEach { lesson ->

                    Card(
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {

                            Text(
                                text = "Пара ${lesson.lessonNumber}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = lesson.time,
                                style = MaterialTheme.typography.bodySmall
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            lesson.groupParts.forEach { (_, info) ->
                                if (info != null) {
                                    Text(
                                        text = info.subject,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(info.teacher)
                                    Text("${info.building}, ${info.classroom}")
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
