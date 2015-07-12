package joe.wethr.Objects;

import java.util.HashMap;

/**
 * Created by Joe on 6/24/2015.
 */
public class IconDescription {

    String[] rainWords = {"rain","drizzle","thunderstorm","mist","downpour"};
    String[] snowWords = {"snow","sleet"};
    HashMap<Integer,String> ids;



    public IconDescription(){
        ids = new HashMap<>();
        
        ids.put(200,"Thunderstorm with teeny amount of rain.");
        ids.put(201,"Thunderstorm with a good amount of rain.");
        ids.put(202,"Thunderstorm with heavy rain.");
        ids.put(210,"Teeny tiny thunderstorm.");
        ids.put(211,"Run-of-the-mill thunderstorm.");
        ids.put(212,"Pretty serious thunderstorm. Watch out!");
        ids.put(221,"Raggedy thunderstorm.");
        ids.put(230,"Thunderstorm with a teeny amount of drizzle.");
        ids.put(231,"Thunderstorm with a good amount of drizzle.");
        ids.put(232,"Thunderstorm with heavy drizzle.");



        ids.put(300,"A teeny amount of drizzle rain.");
        ids.put(301,"Run-of-the-mill drizzle.");
        ids.put(302,"Heavy drizzle.");
        ids.put(310,"A teeny amount of drizzle rain.");
        ids.put(311,"Drizzle rain.");
        ids.put(312,"Heavy drizzle.");
        ids.put(313,"A good amount of rain and drizzle.");
        ids.put(314,"Lots of rain and drizzle.");
        ids.put(321,"A good old-fashioned downpour.");


        ids.put(500,"A teeny amount of rain.");
        ids.put(501,"A good amount of rain.");
        ids.put(502,"Heavy rain.");
        ids.put(503,"Very heavy rain. Bring two umbrellas!");
        ids.put(504,"Extreme rain. Bring three umbrellas!");
        ids.put(511,"Freezing rain. Bring an umbrella and a coat!");
        ids.put(520,"A teeny amount of shower rain.");
        ids.put(521,"A good amount of shower rain.");
        ids.put(522,"Heavy shower rain.");
        ids.put(531,"Raggedy shower rain.");

        ids.put(600,"A teeny amount of snow.");
        ids.put(601,"A good amount of snow.");
        ids.put(602,"Heavy snow!");
        ids.put(611,"Sleet. I'm so sorry.");
        ids.put(612,"Shower sleet. Gross.");
        ids.put(615,"Light rain AND snow?! That sucks.");
        ids.put(616,"A good amount rain AND snow?! That really sucks.");
        ids.put(620,"A teeny amount of shower snow.");
        ids.put(621,"A good amount of shower snow.");
        ids.put(622,"Lots of shower snow. Stay inside today.");


        ids.put(701,"A little misty out today. Better watch pokemon.");
        ids.put(711,"Smoke?! Is there a fire?!");
        ids.put(721,"Hazy.");
        ids.put(731,"Sandy dust whirls?! Where are you? A beach?");
        ids.put(741,"It's foggy out today.");
        ids.put(751,"Sand?");
        ids.put(761,"It's kind of dusty out today.");
        ids.put(762,"Did a volcano just erupt?");
        ids.put(771,"What is a squall?");
        ids.put(781,"Tornado. It's probably better to just stay inside.");

        ids.put(800,"Clear sky. Go outside!");
        ids.put(801,"A few clouds. Go outside!");
        ids.put(802,"Scattered clouds. Go cloud watch!");
        ids.put(803,"Broken clouds. Go fix them!");
        ids.put(804,"Overcast clouds. Probably a beautiful day outside.");

        ids.put(900,"TORNADO!!!");
        ids.put(901,"TROPICAL STORM!!!");
        ids.put(902,"HURRICANE!!!");
        ids.put(903,"COLD!!!");
        ids.put(904,"HOT!!!");
        ids.put(905,"WINDY!!!");
        ids.put(906,"HAIL!!!");

        ids.put(951,"A calm beautiful day. Go outside!");
        ids.put(952,"There's a light breeze outside today.");
        ids.put(953,"There's a gentle breeze. Go fly a kite.");
        ids.put(954,"Moderate breeze. Go fly a kite!");
        ids.put(955,"Fresh breeze. How nice :)");
        ids.put(956,"Strong breeze.");
        ids.put(957,"High wind, near gale. Watch out!");
        ids.put(958,"Gale. Watch out!");
        ids.put(959,"Severe gale. Watch out!");
        ids.put(960,"Bad storm. Be careful!");
        ids.put(961,"Violent storm. Be careful!");
        ids.put(962,"Hurricane!!!");
    }

    public String getDescription(int id){
        return ids.get(id);
    }

    public String[] getSnowWords(){
        return snowWords;
    }

    public String[] getRainWords(){
        return rainWords;
    }















}
