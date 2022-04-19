package com.donny.dendrofinance.util;

import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;

public interface ExportableToJson {
    JsonObject export() throws JsonFormattingException;
}
