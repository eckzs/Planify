package com.app.planify.logic.utils

import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.HtmlCompat

fun htmlToAnnotatedString(html: String): AnnotatedString {
    val preprocessed = preprocessLists(html)
    val spanned = HtmlCompat.fromHtml(preprocessed, HtmlCompat.FROM_HTML_MODE_COMPACT)

    return buildAnnotatedString {
        append(spanned.toString())

        spanned.getSpans(0, spanned.length, Any::class.java).forEach { span ->
            val start = spanned.getSpanStart(span)
            val end = spanned.getSpanEnd(span)
            when (span) {
                is StyleSpan -> when (span.style) {
                    android.graphics.Typeface.BOLD -> addStyle(
                        SpanStyle(fontWeight = FontWeight.Bold), start, end
                    )
                    android.graphics.Typeface.ITALIC -> addStyle(
                        SpanStyle(fontStyle = FontStyle.Italic), start, end
                    )
                    android.graphics.Typeface.BOLD_ITALIC -> addStyle(
                        SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic), start, end
                    )
                }
                is UnderlineSpan -> addStyle(
                    SpanStyle(textDecoration = TextDecoration.Underline), start, end
                )
            }
        }
    }
}

private fun preprocessLists(html: String): String {
    var result = html

    val olRegex = Regex("<ol[^>]*>(.*?)</ol>", RegexOption.DOT_MATCHES_ALL)
    result = olRegex.replace(result) { match ->
        val listContent = match.groupValues[1]
        var index = 1
        val items = Regex("<li[^>]*>(.*?)</li>", RegexOption.DOT_MATCHES_ALL)
            .findAll(listContent)
            .map { "${index++}. ${it.groupValues[1]}" }
            .joinToString("<br>")
        items
    }

    val ulRegex = Regex("<ul[^>]*>(.*?)</ul>", RegexOption.DOT_MATCHES_ALL)
    result = ulRegex.replace(result) { match ->
        val listContent = match.groupValues[1]
        val items = Regex("<li[^>]*>(.*?)</li>", RegexOption.DOT_MATCHES_ALL)
            .findAll(listContent)
            .map { "• ${it.groupValues[1]}" }
            .joinToString("<br>")
        items
    }

    return result
}
