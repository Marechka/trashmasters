package com.app.trashmasters.bin.model;

/**
 * Zone classification for bins — drives pickup priority in route generation.
 *
 * COMMERCIAL  — restaurants, malls, food courts. Fill fast, smell fast. Highest priority.
 * PUBLIC      — parks, bus stops, downtown sidewalks. Visible to public, affects city image.
 */
public enum BinZone {
    COMMERCIAL,
    PUBLIC
}
