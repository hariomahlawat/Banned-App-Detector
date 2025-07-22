package com.hariomahlawat.bannedappdetector

/**
 * List of monitored apps with their known package names.
 * Package names are best effort and may not cover all variants.
 */
val bannedAppsList = listOf(
    // A
    MonitoredAppMeta("com.alibaba.aliexpress", "AliExpress"),
    MonitoredAppMeta("com.eg.android.AlipayGphone", "AliPay"),
    MonitoredAppMeta("com.apusapps.browser", "APUS Browser"),
    MonitoredAppMeta("com.apusapps.launcher", "APUS Launcher"),
    MonitoredAppMeta("com.azarlive.android", "Azar"),
    // B
    MonitoredAppMeta("com.baidu.BaiduMap", "Baidu Map"),
    MonitoredAppMeta("com.baidu.translate", "Baidu Translate"),
    MonitoredAppMeta("com.badoo.mobile", "Badoo"),
    MonitoredAppMeta("com.banggood.client", "Banggood"),
    MonitoredAppMeta("com.commsource.beautyplus", "BeautyPlus"),
    MonitoredAppMeta("com.meitu.beautyplusme", "BeautyPlus Me"),
    MonitoredAppMeta("com.meitu.beautycamera", "BeautyCam"),
    MonitoredAppMeta("sg.bigo.live", "Bigo Live"),
    MonitoredAppMeta("video.like", "Likee"),
    MonitoredAppMeta("com.bumble.app", "Bumble"),
    // C
    MonitoredAppMeta("com.intsig.camscanner", "CamScanner"),
    MonitoredAppMeta("com.intsig.camscannerhd", "CamScanner HD"),
    MonitoredAppMeta("com.intsig.camscannerlite", "CamScanner Lite"),
    MonitoredAppMeta("com.clubfactory.app", "Club Factory"),
    MonitoredAppMeta("com.cleanmaster.mguard", "Clean Master"),
    MonitoredAppMeta("com.ksmobile.cb", "CM Browser"),
    MonitoredAppMeta("com.cleanmaster.security", "CM Security"),
    MonitoredAppMeta("com.cmcm.lite", "CM Lite"),
    MonitoredAppMeta("com.coffeemeetsbagel", "Coffee Meets Bagel"),
    MonitoredAppMeta("com.couchsurfing.mobile.android", "Couchsurfing"),
    MonitoredAppMeta("com.elex.cok.gp", "Clash of Kings"),
    // D
    MonitoredAppMeta("in.dailyhunt", "Dailyhunt"),
    MonitoredAppMeta("com.dianxinos.dxbs", "DU Battery Saver"),
    MonitoredAppMeta("com.duapps.cleaner", "DU Cleaner"),
    MonitoredAppMeta("com.duapps.recorder", "DU Recorder"),
    MonitoredAppMeta("com.duapps.applock", "DU Privacy"),
    MonitoredAppMeta("com.duapps.browser", "DU Browser"),
    // E
    MonitoredAppMeta("co.ello.Ello", "Ello"),
    MonitoredAppMeta("com.estrongs.android.pop", "ES File Explorer"),
    // F
    MonitoredAppMeta("com.facebook.katana", "Facebook"),
    MonitoredAppMeta("com.facebook.lite", "Facebook Lite"),
    MonitoredAppMeta("com.facebook.orca", "Messenger"),
    MonitoredAppMeta("com.facebook.pages.app", "Pages Manager"),
    MonitoredAppMeta("com.facebook.adsmanager", "Meta Ads Manager"),
    MonitoredAppMeta("com.friendsfeed", "FriendsFeed"),
    // G
    MonitoredAppMeta("com.globalegrow.app.gearbest", "Gearbest"),
    // H
    MonitoredAppMeta("com.ftw_and_co.happn", "Happn"),
    MonitoredAppMeta("in.helloapp", "Helo"),
    MonitoredAppMeta("com.bsb.hike", "Hike"),
    MonitoredAppMeta("co.hinge.app", "Hinge"),
    MonitoredAppMeta("com.hungama.myplay.activity", "Hungama Music"),
    // I
    MonitoredAppMeta("com.imo.android.imoim", "IMO"),
    MonitoredAppMeta("com.instagram.android", "Instagram"),
    MonitoredAppMeta("com.instagram.barcelona", "Threads"),
    MonitoredAppMeta("com.instagram.layout", "Layout"),
    MonitoredAppMeta("com.instagram.boomerang", "Boomerang"),
    // K
    MonitoredAppMeta("com.kwai.video", "Kwai"),
    // L
    MonitoredAppMeta("jp.naver.line.android", "Line"),
    MonitoredAppMeta("com.cmcm.live", "LiveMe"),
    // M
    MonitoredAppMeta("com.mi.global.shop", "Mi Store"),
    MonitoredAppMeta("com.mi.global.bbs", "Mi Community"),
    MonitoredAppMeta("com.xiaomi.mivideocall", "Mi Video Call"),
    MonitoredAppMeta("com.mi.globalbrowser", "Mi Browser"),
    MonitoredAppMeta("com.xiaomi.mipicks", "GetApps"),
    MonitoredAppMeta("com.modlily.android", "Modlily"),
    // N
    MonitoredAppMeta("com.nimbuzz", "Nimbuzz"),
    MonitoredAppMeta("com.nono.android", "NONO Live"),
    MonitoredAppMeta("com.newsdog", "NewsDog"),
    // O
    MonitoredAppMeta("com.okcupid.app", "OkCupid"),
    // P
    MonitoredAppMeta("com.lbe.parallel.intl", "Parallel Space"),
    MonitoredAppMeta("com.popxo", "POPxo"),
    MonitoredAppMeta("com.pratilipi.mobile", "Pratilipi"),
    MonitoredAppMeta("com.tencent.ig", "PUBG Mobile"),
    MonitoredAppMeta("com.tencent.iglite", "PUBG Mobile Lite"),
    MonitoredAppMeta("com.pubg.newstate", "PUBG NEW STATE"),
    MonitoredAppMeta("com.tencent.arenabreakout", "Arena Breakout"),
    MonitoredAppMeta("com.activision.callofduty.shooter", "Call of Duty Mobile"),
    // Q
    MonitoredAppMeta("com.tencent.mobileqq", "QQ"),
    MonitoredAppMeta("com.tencent.qqmusic", "QQ Music"),
    MonitoredAppMeta("com.tencent.qqmail", "QQ Mail"),
    MonitoredAppMeta("com.tencent.qqlive", "QQ Player"),
    MonitoredAppMeta("com.tencent.mtt", "QQ Browser"),
    MonitoredAppMeta("com.tencent.wesync", "WeSync"),
    MonitoredAppMeta("com.qzone", "Qzone"),
    // R
    MonitoredAppMeta("com.reddit.frontpage", "Reddit"),
    MonitoredAppMeta("com.romwe", "Romwe"),
    MonitoredAppMeta("com.rosegal", "Rosegal"),
    // S
    MonitoredAppMeta("com.lenovo.anyshare.gps", "SHAREit"),
    MonitoredAppMeta("shareit.lite", "SHAREit Lite"),
    MonitoredAppMeta("com.zzkko", "Shein"),
    MonitoredAppMeta("com.campmobile.snow", "Snow"),
    MonitoredAppMeta("com.snapchat.android", "Snapchat"),
    MonitoredAppMeta("com.songspk.app", "Songs.pk"),
    MonitoredAppMeta("com.melodis.midomiMusicIdentifier.freemium", "SoundHound"),
    // T
    MonitoredAppMeta("com.zhiliaoapp.musically", "TikTok"),
    MonitoredAppMeta("com.zhiliaoapp.musically.go", "TikTok Lite"),
    MonitoredAppMeta("com.lemon.lvoverseas", "CapCut"),
    MonitoredAppMeta("com.lemon.lvoverseas.lite", "Lemon8"),
    MonitoredAppMeta("com.p1.mobile.putong", "Tantan"),
    MonitoredAppMeta("com.tatteam.totalk", "ToTok"),
    MonitoredAppMeta("com.truecaller", "Truecaller"),
    MonitoredAppMeta("com.truecaller.lite", "Truecaller Lite"),
    MonitoredAppMeta("com.tumblr", "Tumblr"),
    MonitoredAppMeta("com.tinder", "Tinder"),
    MonitoredAppMeta("com.tinder.lite", "Tinder Lite"),
    MonitoredAppMeta("com.trulymadly.android.app", "TrulyMadly"),
    // U
    MonitoredAppMeta("com.UCMobile.intl", "UC Browser"),
    MonitoredAppMeta("com.uc.browser.en", "UC Browser Mini"),
    MonitoredAppMeta("com.ucturbo", "UC Turbo"),
    MonitoredAppMeta("com.uc.iflow", "UC News"),
    MonitoredAppMeta("com.asiainno.uplive", "Uplive"),
    // V
    MonitoredAppMeta("com.viber.voip", "Viber"),
    MonitoredAppMeta("com.vimo.videoeditor", "Vimo"),
    MonitoredAppMeta("com.nebula.vigo", "Vigo Video"),
    MonitoredAppMeta("com.vmate", "Vmate"),
    MonitoredAppMeta("com.quvideo.xiaoying", "VivaVideo"),
    MonitoredAppMeta("com.quvideo.xiaoying.pro", "VivaVideo Editor"),
    MonitoredAppMeta("app.vokal.india", "Vokal"),
    MonitoredAppMeta("com.netqin.ps", "Vault-Hide"),
    // W
    MonitoredAppMeta("com.tencent.mm", "WeChat"),
    MonitoredAppMeta("com.tencent.wework", "WeChat Work"),
    MonitoredAppMeta("com.tencent.mm.small", "WeChat Lite"),
    MonitoredAppMeta("com.sina.weibo", "Weibo"),
    MonitoredAppMeta("com.baidu.wondercamera", "Wonder Camera"),
    MonitoredAppMeta("cn.jingling.motu.photowonder", "PhotoWonder"),
    MonitoredAppMeta("com.doubleyou.w", "Woo"),
    // X
    MonitoredAppMeta("cn.xender", "Xender"),
    // Y
    MonitoredAppMeta("com.yelp.android", "Yelp"),
    // Z
    MonitoredAppMeta("us.zoom.videomeetings", "Zoom")
)

val bannedAppsAZ: Map<Char, List<String>> =
    bannedAppsList.map { it.displayName }.groupBy { it.first().uppercaseChar() }
