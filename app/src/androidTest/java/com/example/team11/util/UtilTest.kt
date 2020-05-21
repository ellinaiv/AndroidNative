package com.example.team11.util

import com.example.team11.database.entity.Place
import org.junit.Assert.*
import org.junit.Test


class UtilTest{


    val placeGrorud = Place(701, "Badedammen ved Grorud", 59.975274, 10.885098, Int.MAX_VALUE)
    val placeBekkensten = Place(702, "Bekkensten", 59.792160, 10.733422, Int.MAX_VALUE)
    val placeBestemorstranda = Place(703, "Bestemorstranda", 59.826644, 10.758245, Int.MAX_VALUE)
    val placeBogstadvannet = Place(704, "Bogstadvannet", 59.969994, 10.636734, Int.MAX_VALUE)
    val placeBrekkedammen = Place(705, "Brekkedammen (Frysja)", 59.967367, 10.779186, Int.MAX_VALUE)
    val answer = listOf(placeGrorud, placeBekkensten, placeBestemorstranda,
        placeBogstadvannet, placeBrekkedammen)

    val withoutTemp = """<?xml version="1.0" encoding="UTF-8"?>
    <badetemp xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos#" xml:lang="en">
    	<credit>
    		<!-- For å bruke åpne/gratis badetemperaturdata fra Oslo kommune, MÅ du vise følgende tekst godt synlig på nettsiden din. Teksten skal være en lenke til URL-en som er spesifisert.-->
    		<!-- Les mer om vilkår for bruk av gratis badetemperaturdata + retningslinjer på http://www.bymiljoetaten.oslo.kommune.no/om_bymiljoetaten/apne_data/ -->
    		<link text="Badetemperaturer levert av Oslo kommune" url="http://http://www.bymiljoetaten.oslo.kommune.no/article208291.html"/>
    	</credit>
    	<!-- Tidspunkt for sist registrerte måling av badetemperatur: -->
    	<lastupdate>1970-01-01T01:00:00</lastupdate>
    	<place id="701">
    		<name><![CDATA[Badedammen ved Grorud]]></name>
    		<link>http://www.bymiljoetaten.oslo.kommune.no/article208284.html</link>
    		<geo:lat>59.975274</geo:lat>
    		<geo:long>10.885098</geo:long>
    		<temp_vann></temp_vann>
    		<updated></updated>
    	</place>
    	<place id="702">
    		<name><![CDATA[Bekkensten]]></name>
    		<link>http://www.bymiljoetaten.oslo.kommune.no/article208285.html</link>
    		<geo:lat>59.792160</geo:lat>
    		<geo:long>10.733422</geo:long>
    		<temp_vann></temp_vann>
    		<updated></updated>
    	</place>
    	<place id="703">
    		<name><![CDATA[Bestemorstranda]]></name>
    		<link>http://www.bymiljoetaten.oslo.kommune.no/article208287.html</link>
    		<geo:lat>59.826644</geo:lat>
    		<geo:long>10.758245</geo:long>
    		<temp_vann></temp_vann>
    		<updated></updated>
    	</place>
    	<place id="704">
    		<name><![CDATA[Bogstadvannet]]></name>
    		<link>http://www.bymiljoetaten.oslo.kommune.no/article208288.html</link>
    		<geo:lat>59.969994</geo:lat>
    		<geo:long>10.636734</geo:long>
    		<temp_vann></temp_vann>
    		<updated></updated>
    	</place>
    	<place id="705">
    		<name><![CDATA[Brekkedammen (Frysja)]]></name>
    		<link>http://www.bymiljoetaten.oslo.kommune.no/article208289.html</link>
    		<geo:lat>59.967367</geo:lat>
    		<geo:long>10.779186</geo:long>
    		<temp_vann></temp_vann>
    		<updated></updated>
    	</place>
    </badetemp>
    """

    val withTemp = """<?xml version="1.0" encoding="UTF-8"?>
    <badetemp xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos#" xml:lang="en">
    	<credit>
    		<!-- For å bruke åpne/gratis badetemperaturdata fra Oslo kommune, MÅ du vise følgende tekst godt synlig på nettsiden din. Teksten skal være en lenke til URL-en som er spesifisert.-->
    		<!-- Les mer om vilkår for bruk av gratis badetemperaturdata + retningslinjer på http://www.bymiljoetaten.oslo.kommune.no/om_bymiljoetaten/apne_data/ -->
    		<link text="Badetemperaturer levert av Oslo kommune" url="http://http://www.bymiljoetaten.oslo.kommune.no/article208291.html"/>
    	</credit>
    	<!-- Tidspunkt for sist registrerte måling av badetemperatur: -->
    	<lastupdate>1970-01-01T01:00:00</lastupdate>
    	<place id="701">
    		<name><![CDATA[Badedammen ved Grorud]]></name>
    		<link>http://www.bymiljoetaten.oslo.kommune.no/article208284.html</link>
    		<geo:lat>59.975274</geo:lat>
    		<geo:long>10.885098</geo:long>
    		<temp_vann>12</temp_vann>
    		<updated></updated>
    	</place>
    	<place id="702">
    		<name><![CDATA[Bekkensten]]></name>
    		<link>http://www.bymiljoetaten.oslo.kommune.no/article208285.html</link>
    		<geo:lat>59.792160</geo:lat>
    		<geo:long>10.733422</geo:long>
    		<temp_vann>13</temp_vann>
    		<updated></updated>
    	</place>
    	<place id="703">
    		<name><![CDATA[Bestemorstranda]]></name>
    		<link>http://www.bymiljoetaten.oslo.kommune.no/article208287.html</link>
    		<geo:lat>59.826644</geo:lat>
    		<geo:long>10.758245</geo:long>
    		<temp_vann>14</temp_vann>
    		<updated></updated>
    	</place>
    	<place id="704">
    		<name><![CDATA[Bogstadvannet]]></name>
    		<link>http://www.bymiljoetaten.oslo.kommune.no/article208288.html</link>
    		<geo:lat>59.969994</geo:lat>
    		<geo:long>10.636734</geo:long>
    		<temp_vann>15</temp_vann>
    		<updated></updated>
    	</place>
    	<place id="705">
    		<name><![CDATA[Brekkedammen (Frysja)]]></name>
    		<link>http://www.bymiljoetaten.oslo.kommune.no/article208289.html</link>
    		<geo:lat>59.967367</geo:lat>
    		<geo:long>10.779186</geo:long>
    		<temp_vann>16</temp_vann>
    		<updated></updated>
    	</place>
    </badetemp>
    """

    val withAndWithoutTemp = """<?xml version="1.0" encoding="UTF-8"?>
    <badetemp xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos#" xml:lang="en">
    	<credit>
    		<!-- For å bruke åpne/gratis badetemperaturdata fra Oslo kommune, MÅ du vise følgende tekst godt synlig på nettsiden din. Teksten skal være en lenke til URL-en som er spesifisert.-->
    		<!-- Les mer om vilkår for bruk av gratis badetemperaturdata + retningslinjer på http://www.bymiljoetaten.oslo.kommune.no/om_bymiljoetaten/apne_data/ -->
    		<link text="Badetemperaturer levert av Oslo kommune" url="http://http://www.bymiljoetaten.oslo.kommune.no/article208291.html"/>
    	</credit>
    	<!-- Tidspunkt for sist registrerte måling av badetemperatur: -->
    	<lastupdate>1970-01-01T01:00:00</lastupdate>
    	<place id="701">
    		<name><![CDATA[Badedammen ved Grorud]]></name>
    		<link>http://www.bymiljoetaten.oslo.kommune.no/article208284.html</link>
    		<geo:lat>59.975274</geo:lat>
    		<geo:long>10.885098</geo:long>
    		<temp_vann>12</temp_vann>
    		<updated></updated>
    	</place>
    	<place id="702">
    		<name><![CDATA[Bekkensten]]></name>
    		<link>http://www.bymiljoetaten.oslo.kommune.no/article208285.html</link>
    		<geo:lat>59.792160</geo:lat>
    		<geo:long>10.733422</geo:long>
    		<temp_vann>13</temp_vann>
    		<updated></updated>
    	</place>
    	<place id="703">
    		<name><![CDATA[Bestemorstranda]]></name>
    		<link>http://www.bymiljoetaten.oslo.kommune.no/article208287.html</link>
    		<geo:lat>59.826644</geo:lat>
    		<geo:long>10.758245</geo:long>
    		<temp_vann></temp_vann>
    		<updated></updated>
    	</place>
    	<place id="704">
    		<name><![CDATA[Bogstadvannet]]></name>
    		<link>http://www.bymiljoetaten.oslo.kommune.no/article208288.html</link>
    		<geo:lat>59.969994</geo:lat>
    		<geo:long>10.636734</geo:long>
    		<temp_vann>15</temp_vann>
    		<updated></updated>
    	</place>
    	<place id="705">
    		<name><![CDATA[Brekkedammen (Frysja)]]></name>
    		<link>http://www.bymiljoetaten.oslo.kommune.no/article208289.html</link>
    		<geo:lat>59.967367</geo:lat>
    		<geo:long>10.779186</geo:long>
    		<temp_vann></temp_vann>
    		<updated></updated>
    	</place>
    </badetemp>
    """


    @Test
    fun testWithoutTemp(){
        val placesFromXML = Util.parseXMLPlace(withoutTemp)
        assertEquals(answer, placesFromXML)
    }

    @Test
    fun testWithTemp(){
        var i = 12
        answer.forEach {
            it.tempWater = i++
        }
        val placesFromXML = Util.parseXMLPlace(withTemp)
        assertEquals(answer, placesFromXML)
    }

    @Test
    fun testWithAndWithoutTemp(){
        placeGrorud.tempWater = 12
        placeBekkensten.tempWater = 13
        placeBogstadvannet.tempWater = 15

        val placesFromXML = Util.parseXMLPlace(withAndWithoutTemp)
        assertEquals(answer, placesFromXML)
    }








}