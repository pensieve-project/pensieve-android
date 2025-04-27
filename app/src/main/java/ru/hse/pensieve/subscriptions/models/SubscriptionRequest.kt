package ru.hse.pensieve.subscriptions.models

import java.util.UUID

data class SubscriptionRequest(
    val subscriberId: UUID?,
    val targetId: UUID?
)