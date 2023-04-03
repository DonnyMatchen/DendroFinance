package com.donny.dendroroot.util;

import com.donny.dendroroot.json.JsonFormattingException;
import com.donny.dendroroot.json.JsonItem;

public interface ExportableToJson {
    JsonItem export() throws JsonFormattingException;
}
