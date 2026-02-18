package com.app.trashmasters.bin.model;

public enum BinStatus {
    NORMAL,       // 0% - 70% Full
    FULL,         // 70% - 90% Full (Needs pickup soon)
    CRITICAL,     // > 90% Full (Overflow Risk!)
    MAINTENANCE   // Sensor broken or physical damage flagged
}