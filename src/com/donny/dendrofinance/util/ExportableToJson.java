package com.donny.dendrofinance.util;

import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonItem;

public interface ExportableToJson {
    JsonItem export() throws JsonFormattingException;
}
