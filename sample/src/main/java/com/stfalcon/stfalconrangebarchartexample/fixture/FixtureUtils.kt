package com.stfalcon.stfalconrangebarchartexample.fixture

import com.stfalcon.pricerangebar.model.BarEntry
import java.util.*

class FixtureUtils {
    companion object {

        fun prepareSeekBarFixtures(): ArrayList<BarEntry> {
            val entries: ArrayList<BarEntry> = ArrayList()

            val array = arrayOf(
                30.0f to 5.0f, 32.0f to 5.0f, 34.0f to 10.0f, 36.0f to 10.0f, 38.0f to 11.0f, 40.0f to 11.0f,
                42.0f to 15.0f, 44.0f to 15.0f, 46.0f to 20.0f, 48.0f to 20.0f, 50.0f to 21.0f, 52.0f to 21.0f,
                54.0f to 20.0f, 56.0f to 19.0f, 58.0f to 21.0f, 60.0f to 22.0f, 62.0f to 23.0f, 64.0f to 22.0f,
                66.0f to 23.0f, 68.0f to 24.0f, 70.0f to 23.0f, 72.0f to 23.0f, 74.0f to 26.0f, 76.0f to 26.0f,
                78.0f to 29.0f, 80.0f to 29.0f, 82.0f to 30.0f, 84.0f to 30.0f, 86.0f to 32.0f, 88.0f to 32.0f,
                90.0f to 38.0f, 92.0f to 38.0f, 94.0f to 40.0f, 96.0f to 40.0f, 98.0f to 37.0f, 100.0f to 37.0f,
                102.0f to 37.0f, 104.0f to 37.0f, 106.0f to 33.0f, 108.0f to 33.0f, 110.0f to 28.0f, 112.0f to 28.0f,
                114.0f to 23.0f, 116.0f to 23.0f, 118.0f to 22.0f, 120.0f to 22.0f, 122.0f to 17.0f, 124.0f to 17.0f,
                126.0f to 16.0f, 128.0f to 14.0f, 130.0f to 13.0f, 132.0f to 11.0f, 134.0f to 10.0f, 136.0f to 9.0f,
                138.0f to 9.0f, 140.0f to 8.0f, 142.0f to 7.0f, 144.0f to 7.0f, 146.0f to 8.0f, 148.0f to 6.0f,
                150.0f to 6.0f, 152.0f to 5.0f, 154.0f to 5.0f, 156.0f to 6.0f, 158.0f to 6.0f, 160.0f to 4.0f,
                162.0f to 2.0f, 164.0f to 2.0f, 166.0f to 3.0f, 168.0f to 4.0f, 170.0f to 3.0f, 172.0f to 2.0f,
                174.0f to 1.0f, 176.0f to 1.0f, 178.0f to 0.0f, 180.0f to 0.0f, 182.0f to 0.0f, 184.0f to 0.0f
            ).map { BarEntry(it.first, it.second) }

            entries.addAll(array)
            return entries
        }

        fun prepareAreaFixtures(): ArrayList<BarEntry> {
            val entries: ArrayList<BarEntry> = ArrayList()
            val array = arrayOf(
                30.0f to  5.0f, 32.0f to  5.0f, 35.0f to 10.0f, 38.0f to 10.0f, 40.0f to 11.0f, 42.0f to 11.0f,
                45.0f to 15.0f, 48.0f to 15.0f, 50.0f to 20.0f, 52.0f to 20.0f, 55.0f to 21.0f, 58.0f to 21.0f,
                60.0f to 20.0f, 62.0f to 19.0f, 65.0f to 21.0f, 68.0f to 22.0f, 70.0f to 23.0f, 72.0f to 22.0f,
                75.0f to 23.0f, 78.0f to 24.0f, 80.0f to 23.0f, 82.0f to 23.0f, 85.0f to 26.0f, 88.0f to 26.0f,
                90.0f to 29.0f, 92.0f to 29.0f, 95.0f to 30.0f, 98.0f to 30.0f, 100.0f to 32.0f, 102.0f to 32.0f,
                105.0f to 38.0f, 108.0f to 38.0f, 110.0f to 40.0f, 112.0f to 40.0f, 115.0f to 37.0f, 118.0f to 37.0f,
                120.0f to 37.0f, 122.0f to 37.0f, 125.0f to 33.0f, 128.0f to 33.0f, 130.0f to 28.0f, 132.0f to 28.0f,
                135.0f to 23.0f, 138.0f to 23.0f, 140.0f to 22.0f, 142.0f to 22.0f, 145.0f to 17.0f, 148.0f to 17.0f,
                150.0f to 16.0f, 152.0f to 14.0f, 155.0f to 13.0f, 158.0f to 11.0f, 160.0f to 10.0f, 162.0f to 9.0f,
                165.0f to  9.0f, 168.0f to  8.0f, 170.0f to 7.0f,  172.0f to  7.0f, 175.0f to  8.0f, 178.0f to 6.0f,
                180.0f to  6.0f, 182.0f to  5.0f, 185.0f to 5.0f,  188.0f to  6.0f, 190.0f to  6.0f, 192.0f to 4.0f,
                195.0f to  2.0f, 198.0f to  2.0f, 200.0f to 3.0f,  202.0f to  4.0f, 205.0f to  3.0f, 208.0f to 2.0f,
                210.0f to  1.0f, 212.0f to  1.0f, 215.0f to 0.0f,  218.0f to  0.0f, 220.0f to  0.0f, 222.0f to 0.0f
            ).map { BarEntry(it.first, it.second)  }


            entries.addAll(array)
            return entries
        }
    }
}