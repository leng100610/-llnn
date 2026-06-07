package com.example.data

object DefaultData {
    val initialIndicators = listOf(
        EconomicIndicatorEntity(
            id = "GDP",
            name = "國內生產總值年化季率 (GDP)",
            category = "增長指標",
            currentValue = 2.5,
            previousValue = 2.2,
            unit = "%",
            releaseDate = "2026-05-28",
            nextReleaseDate = "2026-06-25",
            trend = "up",
            description = "國內生產總值（GDP）衡量美國在特定時期內生產的所有最終商品及服務的市場總價值。季增率年化數據是衡量整體經濟活動健康狀態與擴張度的最主要溫度計。",
            detailJson = "[{\"date\":\"24 Q3\",\"val\":2.8},{\"date\":\"24 Q4\",\"val\":2.1},{\"date\":\"25 Q1\",\"val\":2.2},{\"date\":\"25 Q2\",\"val\":2.5}]",
            isBookmarked = true
        ),
        EconomicIndicatorEntity(
            id = "CPI",
            name = "消費者物價指數年增率 (CPI)",
            category = "通膨指標",
            currentValue = 2.8,
            previousValue = 2.9,
            unit = "%",
            releaseDate = "2026-05-13",
            nextReleaseDate = "2026-06-12",
            trend = "down",
            description = "消費者物價指數（CPI）是通膨形勢的最直接風向標，反映城鎮消費者購買特定一籃子商品與服務的零售價格變動，是聯準會利率政策極為看重的關鍵依據。",
            detailJson = "[{\"date\":\"12/25\",\"val\":2.9},{\"date\":\"01/26\",\"val\":2.8},{\"date\":\"02/26\",\"val\":2.9},{\"date\":\"03/26\",\"val\":2.8}]",
            isBookmarked = true
        ),
        EconomicIndicatorEntity(
            id = "UNEMPLOYMENT",
            name = "失業率 (Unemployment Rate)",
            category = "就業指標",
            currentValue = 3.9,
            previousValue = 3.8,
            unit = "%",
            releaseDate = "2026-06-05",
            nextReleaseDate = "2026-07-03",
            trend = "up",
            description = "失業率是指處於失業狀態且積極尋求工作的勞動人口占比，是評估勞動力市場鬆緊程度與經濟放緩幅度的滯後但相當具有象徵意義的重要指標。",
            detailJson = "[{\"date\":\"02/26\",\"val\":3.8},{\"date\":\"03/26\",\"val\":3.9},{\"date\":\"04/26\",\"val\":3.8},{\"date\":\"05/26\",\"val\":3.9}]"
        ),
        EconomicIndicatorEntity(
            id = "FED_RATE",
            name = "聯準會基準利率 (Fed Rate)",
            category = "金融指標",
            currentValue = 5.25,
            previousValue = 5.50,
            unit = "%",
            releaseDate = "2026-05-06",
            nextReleaseDate = "2026-06-17",
            trend = "down",
            description = "聯邦基金利率目標區間由 FOMC 決策，是美國貨幣利率的基石。調降基準利率旨在支撐經濟增長，而調升則利於抑制高通膨。",
            detailJson = "[{\"date\":\"12/25\",\"val\":5.50},{\"date\":\"01/26\",\"val\":5.50},{\"date\":\"03/26\",\"val\":5.25},{\"date\":\"05/26\",\"val\":5.25}]",
            isBookmarked = true
        ),
        EconomicIndicatorEntity(
            id = "NON_FARM",
            name = "非農就業人口變動 (Non-Farm)",
            category = "就業指標",
            currentValue = 18.5,
            previousValue = 16.5,
            unit = "萬人",
            releaseDate = "2026-06-05",
            nextReleaseDate = "2026-07-03",
            trend = "up",
            description = "非農就業數據排除農業部門，衡量全美企業及公營機構的新增就業人數。強勁的就業會推升消費信心與通膨氣焰，反之顯示需求放緩。",
            detailJson = "[{\"date\":\"02/26\",\"val\":15.0},{\"date\":\"03/26\",\"val\":16.5},{\"date\":\"04/26\",\"val\":18.5}]"
        ),
        EconomicIndicatorEntity(
            id = "RETAIL_SALES",
            name = "零售銷售月增率 (Retail Sales)",
            category = "消費指標",
            currentValue = 0.3,
            previousValue = -0.1,
            unit = "%",
            releaseDate = "2026-05-15",
            nextReleaseDate = "2026-06-16",
            trend = "up",
            description = "零售銷售由美國商務部發布，常被稱為「恐怖數據」，實時反應百貨商品、餐飲等消費性支出力度。在以消費立國（占 GDP 約七成）的美國至關重要。",
            detailJson = "[{\"date\":\"01/26\",\"val\":0.1},{\"date\":\"02/26\",\"val\":0.5},{\"date\":\"03/26\",\"val\":-0.1},{\"date\":\"04/26\",\"val\":0.3}]"
        ),
        EconomicIndicatorEntity(
            id = "PMI",
            name = "ISM 製造業經理人指數 (PMI)",
            category = "製造指標",
            currentValue = 49.2,
            previousValue = 48.5,
            unit = "點",
            releaseDate = "2026-06-01",
            nextReleaseDate = "2026-07-01",
            trend = "up",
            description = "ISM 製造業採購經理人指數是衡量工業及製造營收週期的領先指標。高於 50 點代表產業擴張；低於 50 分水嶺則代表產業萎縮收縮。",
            detailJson = "[{\"date\":\"02/26\",\"val\":49.1},{\"date\":\"03/26\",\"val\":48.4},{\"date\":\"04/26\",\"val\":48.5},{\"date\":\"05/26\",\"val\":49.2}]"
        ),
        EconomicIndicatorEntity(
            id = "TREASURY_10Y",
            name = "美國10年期公債殖利率 (US10Y)",
            category = "金融指標",
            currentValue = 4.25,
            previousValue = 4.35,
            unit = "%",
            releaseDate = "2026-06-05",
            nextReleaseDate = "每工作日更新",
            trend = "down",
            description = "10年期美債殖利率是全球資產定價之錨，反映市場對長期經濟中性利率、通膨預期與信用風險的共識期盼。與房貸、債券溢價緊密扣連。",
            detailJson = "[{\"date\":\"02/26\",\"val\":4.15},{\"date\":\"03/26\",\"val\":4.25},{\"date\":\"04/26\",\"val\":4.35},{\"date\":\"05/26\",\"val\":4.25}]"
        )
    )

    // 預載預算的預估釋出日期，供事件曆比對
    val economicEvents = listOf(
        EconomicEvent("FOMC 利率決策會議", "2026-06-17", "聯準會今年度第四次利率決策", "中"),
        EconomicEvent("核心 CPI 通膨數據發表", "2026-06-12", "統計上月大眾物資與核心物價年率", "高"),
        EconomicEvent("5月 零售銷售月率 (恐怖數據)", "2026-06-16", "衡量初夏實體與線上核心零售銷售動能", "高"),
        EconomicEvent("Q1 美國 GDP 終值修正公佈", "2026-06-25", "修正前次公布 GDP 下行或上行振幅", "中"),
        EconomicEvent("6月 非農就業人口與失業統計", "2026-07-03", "極度核心指標，牽引下半季聯準會降息預期", "高"),
        EconomicEvent("ISM 製造業 PMI 指數發表", "2026-07-01", "領先指標，衡量工廠及科技採購活動指數", "中")
    )
}

data class EconomicEvent(
    val title: String,
    val date: String,
    val notes: String,
    val impact: String // "高", "中", "低"
)
