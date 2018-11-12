# Stfalcon-PriceRangeBar
<img src="images/swipe_new.gif" width="240" height="420" />
## Who we are
Need iOS and Android apps, MVP development or prototyping? Contact us via info@stfalcon.com. We develop software since 2009, and we're known experts in this field. Check out our portfolio and see more libraries from stfalcon-studio.
## Download
Download via Gradle: 
```compile 'com.github.stfalcon:pricerangebar:0.1.0'```
## Usage
For adding default seekbar with chart just put this code into your layout:
```
<com.stfalcon.pricerangebar.SeekBarWithChart
   android:layout_width="match_parent"
   android:layout_height="wrap_content"/>
```
Or you can use default rangebar with chart just put this code into your layout:
```
<com.stfalcon.pricerangebar.RangeBarWithChart
   android:layout_width="match_parent"
   android:layout_height="wrap_content"/>
```
After that you should to add list entries with data to displaying

```
val seekBarEntries = ArrayList<SeekBarEntry>()

seekBarEntries.add(SeekBarEntry(30.0f, 5.0f))
seekBarEntries.add(SeekBarEntry(32.0f, 7.0f))
seekBarEntries.add(SeekBarEntry(34.0f, 10.0f))
seekBarEntries.add(SeekBarEntry(36.0f, 11.0f))
seekBarEntries.add(SeekBarEntry(38.0f, 14.0f))
seekBarEntries.add(SeekBarEntry(40.0f, 15.0f))

seekBar.setEntries(seekBarEntries)
```
