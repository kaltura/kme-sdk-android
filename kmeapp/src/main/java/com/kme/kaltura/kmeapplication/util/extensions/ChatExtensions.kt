package com.kme.kaltura.kmeapplication.util.extensions

import com.kme.kaltura.kmeapplication.data.MappedChatMessage
import com.stfalcon.chatkit.utils.DateFormatter
import java.util.*
import kotlin.math.abs

const val RECEIVED_MESSAGE_POSITION = -1
private const val NEW_MESSAGE_TIME_RANGE = 5

fun List<MappedChatMessage>.updateChatItemsStyle(): List<MappedChatMessage> {
    return mapIndexed { index, mappedChatMessage ->
        handleItemStyle(index, mappedChatMessage, this)
    }
}

fun handleItemStyle(
    position: Int,
    currentItem: MappedChatMessage,
    items: List<MappedChatMessage>
): MappedChatMessage {
    var style = MappedChatMessage.StyleType.END_MESSAGE_STYLE

    if (position == RECEIVED_MESSAGE_POSITION || position == 0) {
        style = if (isPreviousInTimeRange(position, currentItem.createdAt, items)
            && isPreviousSameAuthor(position, currentItem.user.id, items)
        ) {
            MappedChatMessage.StyleType.END_MESSAGE_STYLE
        } else {
            MappedChatMessage.StyleType.START_MESSAGE_STYLE
        }
    } else if (position > 0 && position < items.size - 1) {
        style = if (isPreviousInTimeRange(position, currentItem.createdAt, items)
            && isPreviousSameAuthor(position, currentItem.user.id, items)
        ) {
            if (isNextInTimeRange(position, currentItem.createdAt, items)
                && isNextSameAuthor(position, currentItem.user.id, items)
            ) {
                MappedChatMessage.StyleType.MIDDLE_MESSAGE_STYLE
            } else {
                MappedChatMessage.StyleType.END_MESSAGE_STYLE
            }
        } else {
            MappedChatMessage.StyleType.START_MESSAGE_STYLE
        }
    }

    currentItem.styleType = style
    return currentItem
}

private fun isNextSameAuthor(
    currentPosition: Int,
    currentUserId: String,
    items: List<MappedChatMessage>
): Boolean {
    val nextPosition = currentPosition - 1
    return if (nextPosition < 0)
        false
    else
        items[nextPosition].user.id.contentEquals(currentUserId)
}

private fun isNextInTimeRange(
    currentPosition: Int,
    dateToCompare: Date,
    items: List<MappedChatMessage>
): Boolean {
    val nextPosition = currentPosition - 1
    if (nextPosition < 0) return false
    val nextPositionDate: Date = items[nextPosition].createdAt
    return DateFormatter.isSameDay(dateToCompare, nextPositionDate)
            && isSameTime(dateToCompare, nextPositionDate)
}

private fun isPreviousSameAuthor(
    currentPosition: Int,
    currentUserId: String,
    items: List<MappedChatMessage>
): Boolean {
    val prevPosition = currentPosition + 1
    return if (prevPosition >= items.size)
        false
    else
        items[prevPosition].user.id.contentEquals(currentUserId)
}


private fun isPreviousInTimeRange(
    currentPosition: Int,
    dateToCompare: Date,
    items: List<MappedChatMessage>
): Boolean {
    val prevPosition = currentPosition + 1
    if (prevPosition >= items.size) return false
    val previousPositionDate: Date = items[prevPosition].createdAt
    return DateFormatter.isSameDay(dateToCompare, previousPositionDate)
            && isSameTime(dateToCompare, previousPositionDate)
}

private fun isSameTime(date1: Date, date2: Date): Boolean {
    val diff = abs(date1.time - date2.time)
    val elapsedMinutes = diff / (60 * 1000)
    return elapsedMinutes <= NEW_MESSAGE_TIME_RANGE

}