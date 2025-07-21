package com.hariomahlawat.bannedappdetector

val bannedAppsList = listOf(
    // A
    "AliExpress", "AliPay", "APUS Browser", "APUS Launcher", "Azar",
    // B
    "Baidu Map", "Baidu Translate", "Badoo", "Banggood", "BeautyPlus", "BeautyPlus Me",
    "BeautyCam", "Bigo Live", "Likee", "Vigo Video", "Bumble",
    // C
    "CamScanner", "CamScanner HD", "CamScanner Lite", "Club Factory",
    "Clean Master", "CM Browser", "CM Security", "CM Lite", "Coffee Meets Bagel",
    "Couchsurfing", "Clash of Kings",
    // D
    "Dailyhunt", "DU Battery Saver", "DU Cleaner", "DU Recorder", "DU Privacy", "DU Browser",
    // E
    "Ello", "ES File Explorer",
    // F
    "Facebook", "Facebook Lite", "Messenger", "Pages Manager", "Meta Ads Manager",
    "FriendsFeed",
    // G
    "Gearbest",
    // H
    "Happn", "Helo", "Hike", "Hinge", "Hungama Music",
    // I
    "IMO", "Instagram", "Threads", "Layout", "Boomerang",
    // K
    "Kwai",
    // L
    "Line", "LiveMe",
    // M
    "Mi Store", "Mi Community", "Mi Video Call", "Mi Browser", "GetApps",
    "Modlily",
    // N
    "Nimbuzz", "NONO Live", "NewsDog",
    // O
    "OkCupid",
    // P
    "Parallel Space", "POPxo", "Pratilipi", "PUBG Mobile", "PUBG Mobile Lite",
    "PUBG NEW STATE", "Arena Breakout", "Call of Duty Mobile",
    // Q
    "QQ", "QQ Music", "QQ Mail", "QQ Player", "QQ Browser", "WeSync", "Qzone",
    // R
    "Reddit", "Romwe", "Rosegal",
    // S
    "SHAREit", "SHAREit Lite", "Shein", "Snow", "Snapchat", "Songs.pk", "SoundHound",
    // T
    "TikTok", "TikTok Lite", "CapCut", "Lemon8", "Tantan", "ToTok", "Truecaller",
    "Truecaller Lite", "Tumblr", "Tinder", "Tinder Lite", "TrulyMadly",
    // U
    "UC Browser", "UC Browser Mini", "UC Turbo", "UC News", "Uplive",
    // V
    "Viber", "Vigo Video", "Vimo", "Vmate", "VivaVideo", "VivaVideo Editor",
    "Vokal", "Vault-Hide",
    // W
    "WeChat", "WeChat Work", "WeChat Lite", "Weibo", "Wonder Camera", "PhotoWonder", "Woo",
    // X
    "Xender",
    // Y
    "Yelp",
    // Z
    "Zoom"
)

val bannedAppsAZ: Map<Char, List<String>> = bannedAppsList.groupBy { it.first().uppercaseChar() }
