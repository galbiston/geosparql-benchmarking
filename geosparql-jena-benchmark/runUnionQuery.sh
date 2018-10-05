#!/bin/bash
gradle run -PappArgs="['tdb_union','micro_union','both']"
gradle run -PappArgs="['tdb_union','../user_defined_macro_union_queries','both']"
