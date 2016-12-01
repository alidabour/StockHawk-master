package com.ali.android.stockhawk.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by sam_chordas on 10/5/15.
 */
@Database(version = QuoteDatabase.VERSION)
class QuoteDatabase {
  private QuoteDatabase(){}

   static final int VERSION = 7;

  @Table(QuoteColumns.class) public static final String QUOTES = "quotes";
  @Table(HistoryColumns.class) public static final String HISTORY = "history";
}
