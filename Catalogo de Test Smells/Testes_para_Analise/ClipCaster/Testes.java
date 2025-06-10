package Testes_para_Analise.ClipCaster;

/*https://github.com/activems/clipcaster/blob/38398d1d047a4064a7017ca8f0d0f3ff0782560c/app/src/androidTest/java/com/actisec/clipcaster/parser/LastPassParserTest.java */

import javax.xml.transform.Source;

public class Testes {

    /*Handling Exeption*/
    public void testInstrumentTimeFunction() throws  Throwable {
        Source source = mTestUtils.readSource(com.actisec.clipcaster.test.R.raw.lastpass_v3);
        JavaScript javaScript = new JavaScript(source.javascriptProgram);
        final long injectedTime = 528698764129L;
        final long expectedTime = injectedTime/1000L;
        String instrumentedTimeFunction = LastPassParser.instrumentTimeFunction(javaScript, injectedTime);
        String fullScriptToRun = "(function(){" + instrumentedTimeFunction + "; return " + LastPassParser.FUNC_GETTIME + "(4)" + ".toFixed()})()";
        String result = evaluate(fullScriptToRun);

        try {
            assertEquals(fullScriptToRun + " produced " + result, expectedTime, Long.parseLong(result));
        } catch (NumberFormatException e){
            throw new RuntimeException(fullScriptToRun + " produced " +result);
        }
    }

    /*Handling Exeption */
    public void testCreateDecryptPrgoram() throws  Throwable {
        Source source = mTestUtils.readSource(com.actisec.clipcaster.test.R.raw.lastpass_v3);
        JavaScript javaScript = new JavaScript(source.javascriptProgram);
        final long injectedTime = source.timeOfNotification;
        final String expectedUsername = "user@example.com";
        final String url = FACEBOOK_URL;
        String decryptProgram = LastPassParser.createDecryptProgram(javaScript, injectedTime);
        String finalProgram = LastPassParser.VAR_INJECTED_URL + " = \"" + url + "\"; " + decryptProgram  + "; " + LastPassParser.FUNC_DECRYPT + "(atob('QBYEQHQESFcIEgkAGlQNCA=='), 61, 4)";

        Log.d("TEST", "Running: " + finalProgram);
        String result = evaluate(finalProgram);

        try {

            assertEquals(finalProgram + " produced " + result, expectedUsername, result);
        } catch (NumberFormatException e){
            throw new RuntimeException(finalProgram + " produced " +result);
        }
    }


    /*Long Method Test(Copilot) Condicional Test Logic */

    public void testCredTmp() throws Throwable{
        DummyCallback handler = new DummyCallback();
        Source source = mTestUtils.readSource(com.actisec.clipcaster.test.R.raw.lastpass_v3);
        JavaScript javaScript = new JavaScript(source.javascriptProgram);
        final long injectedTime = source.timeOfNotification;

        LastPassParser.Parser  parser = new LastPassParser.Parser(getContext(),handler,injectedTime);
        parser.getData(source.javascriptProgram);

        synchronized (handler.data) {
            while (handler.data.isEmpty()) {
                handler.data.wait(2000);
            }
            ScrapedData data = handler.data.remove(0);
            assertNotNull(data);
            final ScrapedCredentials creds = data.creds;
            assertNotNull(creds);

            assertTrue(creds.user != null || creds.pass != null);
            if(creds.user != null){
                assertEquals("user@example.com",creds.user);
            } else if (creds.pass != null){
                assertEquals("p4ssw0rd", creds.pass);
            }
        }
    }






}
