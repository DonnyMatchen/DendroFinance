package com.donny.dendrofinance.util;

import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonItem;
import com.donny.dendrofinance.json.JsonObject;

public interface ExportableToJson {
    JsonItem export() throws JsonFormattingException;
}
