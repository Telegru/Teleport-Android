package ru.tusco.messenger.model

import org.telegram.tgnet.TLRPC

data class DahlWallDialog(
    val message: String = ""
): TLRPC.Dialog()