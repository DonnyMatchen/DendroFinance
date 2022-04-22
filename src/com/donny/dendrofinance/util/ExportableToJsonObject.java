package com.donny.dendrofinance.util;

import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;

public interface ExportableToJsonObject {
    JsonObject export() throws JsonFormattingException;
}
