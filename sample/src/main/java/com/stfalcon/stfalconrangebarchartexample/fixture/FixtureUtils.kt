package com.stfalcon.stfalconrangebarchartexample.fixture

import com.stfalcon.pricerangebar.model.BarEntry
import java.util.*

class FixtureUtils {
    companion object {

        fun prepareSeekBarFixtures(): ArrayList<BarEntry> {
            val entries: ArrayList<BarEntry> = ArrayList()

            val array = arrayOf(
                BarEntry(30.0f, 5.0f), BarEntry(32.0f, 5.0f), BarEntry(34.0f, 10.0f),
                BarEntry(36.0f, 10.0f), BarEntry(38.0f, 11.0f), BarEntry(40.0f, 11.0f),
                BarEntry(42.0f, 15.0f), BarEntry(44.0f, 15.0f), BarEntry(46.0f, 20.0f),
                BarEntry(48.0f, 20.0f), BarEntry(50.0f, 21.0f), BarEntry(52.0f, 21.0f),
                BarEntry(54.0f, 20.0f), BarEntry(56.0f, 19.0f), BarEntry(58.0f, 21.0f),
                BarEntry(60.0f, 22.0f), BarEntry(62.0f, 23.0f), BarEntry(64.0f, 22.0f),
                BarEntry(66.0f, 23.0f), BarEntry(68.0f, 24.0f), BarEntry(70.0f, 23.0f),
                BarEntry(72.0f, 23.0f), BarEntry(74.0f, 26.0f), BarEntry(76.0f, 26.0f),
                BarEntry(78.0f, 29.0f), BarEntry(80.0f, 29.0f), BarEntry(82.0f, 30.0f),
                BarEntry(84.0f, 30.0f), BarEntry(86.0f, 32.0f), BarEntry(88.0f, 32.0f),
                BarEntry(90.0f, 38.0f), BarEntry(92.0f, 38.0f), BarEntry(94.0f, 40.0f),
                BarEntry(96.0f, 40.0f), BarEntry(98.0f, 37.0f), BarEntry(100.0f, 37.0f),
                BarEntry(102.0f, 37.0f), BarEntry(104.0f, 37.0f), BarEntry(106.0f, 33.0f),
                BarEntry(108.0f, 33.0f), BarEntry(110.0f, 28.0f), BarEntry(112.0f, 28.0f),
                BarEntry(114.0f, 23.0f), BarEntry(116.0f, 23.0f), BarEntry(118.0f, 22.0f),
                BarEntry(120.0f, 22.0f), BarEntry(122.0f, 17.0f), BarEntry(124.0f, 17.0f),
                BarEntry(126.0f, 16.0f), BarEntry(128.0f, 14.0f), BarEntry(130.0f, 13.0f),
                BarEntry(132.0f, 11.0f), BarEntry(134.0f, 10.0f), BarEntry(136.0f, 9.0f),
                BarEntry(138.0f, 9.0f), BarEntry(140.0f, 8.0f), BarEntry(142.0f, 7.0f),
                BarEntry(144.0f, 7.0f), BarEntry(146.0f, 8.0f), BarEntry(148.0f, 6.0f),
                BarEntry(150.0f, 6.0f), BarEntry(152.0f, 5.0f), BarEntry(154.0f, 5.0f),
                BarEntry(156.0f, 6.0f), BarEntry(158.0f, 6.0f), BarEntry(160.0f, 4.0f),
                BarEntry(162.0f, 2.0f), BarEntry(164.0f, 2.0f), BarEntry(166.0f, 3.0f),
                BarEntry(168.0f, 4.0f), BarEntry(170.0f, 3.0f), BarEntry(172.0f, 2.0f),
                BarEntry(174.0f, 1.0f), BarEntry(176.0f, 1.0f), BarEntry(178.0f, 0.0f),
                BarEntry(180.0f, 0.0f), BarEntry(182.0f, 0.0f), BarEntry(184.0f, 0.0f)
            )

            entries.addAll(array)
            return entries
        }

        fun prepareAreaFixtures(): ArrayList<BarEntry> {
            val entries: ArrayList<BarEntry> = ArrayList()

            val array = arrayOf(
                BarEntry(30.0f, 5.0f), BarEntry(32.0f, 5.0f), BarEntry(35.0f, 10.0f),
                BarEntry(38.0f, 10.0f), BarEntry(40.0f, 11.0f), BarEntry(42.0f, 11.0f),
                BarEntry(45.0f, 15.0f), BarEntry(48.0f, 15.0f), BarEntry(50.0f, 20.0f),
                BarEntry(52.0f, 20.0f), BarEntry(55.0f, 21.0f), BarEntry(58.0f, 21.0f),
                BarEntry(60.0f, 20.0f), BarEntry(62.0f, 19.0f), BarEntry(65.0f, 21.0f),
                BarEntry(68.0f, 22.0f), BarEntry(70.0f, 23.0f), BarEntry(72.0f, 22.0f),
                BarEntry(75.0f, 23.0f), BarEntry(78.0f, 24.0f), BarEntry(80.0f, 23.0f),
                BarEntry(82.0f, 23.0f), BarEntry(85.0f, 26.0f), BarEntry(88.0f, 26.0f),
                BarEntry(90.0f, 29.0f), BarEntry(92.0f, 29.0f), BarEntry(95.0f, 30.0f),
                BarEntry(98.0f, 30.0f), BarEntry(100.0f, 32.0f), BarEntry(102.0f, 32.0f),
                BarEntry(105.0f, 38.0f), BarEntry(108.0f, 38.0f), BarEntry(110.0f, 40.0f),
                BarEntry(112.0f, 40.0f), BarEntry(115.0f, 37.0f), BarEntry(118.0f, 37.0f),
                BarEntry(120.0f, 37.0f), BarEntry(122.0f, 37.0f), BarEntry(125.0f, 33.0f),
                BarEntry(128.0f, 33.0f), BarEntry(130.0f, 28.0f), BarEntry(132.0f, 28.0f),
                BarEntry(135.0f, 23.0f), BarEntry(138.0f, 23.0f), BarEntry(140.0f, 22.0f),
                BarEntry(142.0f, 22.0f), BarEntry(145.0f, 17.0f), BarEntry(148.0f, 17.0f),
                BarEntry(150.0f, 16.0f), BarEntry(152.0f, 14.0f), BarEntry(155.0f, 13.0f),
                BarEntry(158.0f, 11.0f), BarEntry(160.0f, 10.0f), BarEntry(162.0f, 9.0f),
                BarEntry(165.0f, 9.0f), BarEntry(168.0f, 8.0f), BarEntry(170.0f, 7.0f),
                BarEntry(172.0f, 7.0f), BarEntry(175.0f, 8.0f), BarEntry(178.0f, 6.0f),
                BarEntry(180.0f, 6.0f), BarEntry(182.0f, 5.0f), BarEntry(185.0f, 5.0f),
                BarEntry(188.0f, 6.0f), BarEntry(190.0f, 6.0f), BarEntry(192.0f, 4.0f),
                BarEntry(195.0f, 2.0f), BarEntry(198.0f, 2.0f), BarEntry(200.0f, 3.0f),
                BarEntry(202.0f, 4.0f), BarEntry(205.0f, 3.0f), BarEntry(208.0f, 2.0f),
                BarEntry(210.0f, 1.0f), BarEntry(212.0f, 1.0f), BarEntry(215.0f, 0.0f),
                BarEntry(218.0f, 0.0f), BarEntry(220.0f, 0.0f), BarEntry(222.0f, 0.0f)
            )

            entries.addAll(array)
            return entries
        }
    }
}