/*
 * Lemonade View - Top JavaFX Node  for display of game.
 */
package com.maehem.lemonade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

/**
 *
 * @author mark
 */
public class LemonadeBaseView extends StackPane implements LemonadeStateChangeListener {
    private static final Logger log = Logger.getLogger(LemonadeBaseView.class.getName());

    LemonadeController gameState;
    
    private Sequencer sequencer;
    
    private final static String IMG_LAYERS_PATH     = "/scene-layers";
    
    private final static String SKY_BLUE            = IMG_LAYERS_PATH + "/sky-blue.png";
    private final static String SKY_OVERCAST        = IMG_LAYERS_PATH + "/sky-overcast.png";

    private final static String EARTH               = IMG_LAYERS_PATH + "/earth.png";
   
    private final static String STAND_NORMAL        = IMG_LAYERS_PATH + "/stand-normal.png";
    private final static String STAND_WRECKED       = IMG_LAYERS_PATH + "/stand-wrecked.png";

    private final static String PROP_GLASSES_FULL    = IMG_LAYERS_PATH + "/prop-glasses-full.png";
    private final static String PROP_GLASSES_TRASHED = IMG_LAYERS_PATH + "/prop-glasses-trashed.png";
    private final static String PROP_WORK_CREW       = IMG_LAYERS_PATH + "/prop-work-crew.png";
    
    private final static String WEATHER_CLOUDS_1     = IMG_LAYERS_PATH + "/weather-clouds-1.png";
    private final static String WEATHER_CLOUDS_2     = IMG_LAYERS_PATH + "/weather-clouds-2.png";
    private final static String WEATHER_SUNNY        = IMG_LAYERS_PATH + "/weather-sunny.png";
    private final static String WEATHER_LIGHTNING    = IMG_LAYERS_PATH + "/weather-thunderstorm-lightning.png";
    private final static String WEATHER_RAIN         = IMG_LAYERS_PATH + "/weather-rain.png";

    public LemonadeBaseView(LemonadeController gameState) {
        this.gameState = gameState;
        gameState.addListener(this);       
        renderNormal();
        gameState.gameInit();

        try {
            var synth = MidiSystem.getSynthesizer();
            synth.loadAllInstruments(synth.getDefaultSoundbank());
        } catch (MidiUnavailableException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }
    
    protected void doQuit() {
        // Stop and close MIDI.
        if ( sequencer != null ) {
            log.log(Level.CONFIG, "Shut down MIDI.");
            sequencer.stop();
            sequencer.close();
        }
    }
   
    @Override
    public void gameStateChanged() {
        switch ( gameState.getState() ) {
            case START:
                PauseTransition delay1 = new PauseTransition(Duration.seconds(1));
                delay1.setOnFinished(event -> gameState.setStartFinished());
                delay1.play();
               //renderNormal();
                vivaldiTrack();
            case SPLASH:          
                break;
            case SETUP:
                break;
            case DO_LOAD:
                break;
            case DAY_START:
                // update  weather
                renderCurrentEvents();
                break;
            case SELLING_START:
                renderCurrentEvents();
                PauseTransition delay2 = new PauseTransition(Duration.seconds(2));
                delay2.setOnFinished( event -> gameState.setSellingEnd());
                delay2.play();
                break;
            case SELLING_END:
                renderCurrentEvents();  // They might have changed during the day.
                PauseTransition delay3 = new PauseTransition(Duration.seconds(4));
                delay3.setOnFinished( event -> gameState.setReport());
                delay3.play();
                break;
            case REPORT:
                break;
            case QUIT:
                doQuit();
                System.exit(0);
                break;
        }
        
       
       // Update product display
       
       // Uodate player name
    }
    
    // Update Sky
    private void renderNormal() {
        renderLayers(Arrays.asList(
                new Layer(SKY_BLUE),
                new Layer(WEATHER_CLOUDS_1),
                new Layer(EARTH), 
                new Layer(STAND_NORMAL)
        ));
    }
    
    /**
     * Class for managing String name for layer and game-changeable opacity.
     */
    private class Layer {
        private final String name;
        private double opacity = 1.0;

        public Layer(String name, double opacity) {
            this(name);
            this.opacity = opacity;
        }

        public Layer( String name) {
            this.name = name;
        }
        
       public String getName() {
           return name;
       }
       
       public double getOpacity() {
           return opacity;
       }        
    }
    
    private void renderCurrentEvents() {
        LemonadeModel state = gameState.getModel();
        
        ArrayList<Layer> layers = new ArrayList<>();
        
        switch (state.getSunnyCloudy()) {
            case 5:
                // Thunderstorm
                // Overcast
                layers.add(new Layer(SKY_OVERCAST));
                break;
            case 10:
                // Overcast
                layers.add(new Layer(SKY_BLUE));
                double opacity = state.getDryness();
                layers.add(new Layer(SKY_OVERCAST, 1.0-opacity));
                break;
            default:
                layers.add(new Layer(SKY_BLUE));
                break;
        }
        
        // Final Sky Layers
        // Sunny/Cloudy.  2 = sunny, 5 = thunderstorm, 7 = heatwave, 10 = rain w/ chance of thunderstorm.
        switch (state.getSunnyCloudy() ) {
            case 2:  // sunny
                layers.add(new Layer(WEATHER_SUNNY, 0.6)); // Less intense sun.
                break;
            case 5:  // thunderstorm, trashed
                layers.add(new Layer(WEATHER_LIGHTNING));
                break;
            case 7:  // heatwave
                layers.add(new Layer(WEATHER_SUNNY));  // More intense version of sun.
                break;            
        }
        
        // Clouds based on dryness.
        double dryness = state.getDryness();
        if ( dryness > 1.0 && dryness < 1.5 ) { // faint clouds
            layers.add(new Layer(WEATHER_CLOUDS_1, 0.5));            
        }
        if ( dryness > 0.7 && dryness < 1.0 ) {
            layers.add(new Layer(WEATHER_CLOUDS_1, 0.8));
        }
        if ( dryness <= 0.7 ) {
            layers.add(new Layer(WEATHER_CLOUDS_1, 1.0));
            layers.add(new Layer(WEATHER_CLOUDS_2, 0.6));
        }
        if ( dryness < 0.2 ) {
            layers.add(new Layer(WEATHER_CLOUDS_2));
        }
        
        // Mid layers.  Earth and Stand
        layers.add(new Layer(EARTH));
        if ( state.getSunnyCloudy() == 5 ) {  // Trashed.
            layers.add(new Layer(STAND_WRECKED));
            layers.add(new Layer(PROP_GLASSES_TRASHED));
        } else  {
            layers.add(new Layer(STAND_NORMAL));
        }
        
        // Normally glasses on stand, no glasses if buyout or trashed.
        if ( state.getSunnyCloudy() != 5 && state.getStreetBuyout() == 0  ) {
            layers.add(new Layer(PROP_GLASSES_FULL ));          
        }
        
        if ( state.getStreetWork() ) {
            layers.add(new Layer(PROP_WORK_CREW));
        }
        
        // Overlay rain if present.
        if ( state.getSunnyCloudy() == 5 || state.getSunnyCloudy()  == 10 ) {  // Needs to be on the front layer, so put in last..
            double opacity = 1.0;
            if ( state.getSunnyCloudy() == 10 ) {  // Rain  0.2 - 0.7  dryness
                // Set opacity of rain.
                opacity = 1.2  - state.getDryness()*1.4;   // dryness 0.2:heavy  - 0.7:light           
            }
            Layer layer = new Layer(WEATHER_RAIN, opacity);
            layers.add(layer);
        }
        
        renderLayers(layers);
    }
    
    private void renderLayers( List<Layer> layers) {
        getChildren().clear();
        for ( Layer l : layers ) {
            String path = l.getName();
            Image img = new Image(getClass().getResourceAsStream(path));
            ImageView imgView = new ImageView(img);           
            getChildren().add(imgView);
            imgView.setOpacity(l.getOpacity());
        }
    }
    
    private void vivaldiTrack() {
        try {
            Sequence sequence = MidiSystem.getSequence(getClass().getResource("/midi/vivaldi-spring.mid").openStream());
            
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setSequence(sequence);
            sequencer.setTempoFactor(1f);
            sequencer.addMetaEventListener(meta -> {
                if (meta.getType() == 47) { // track end
                    sequencer.setTickPosition(0);
                    sequencer.start();
                }
            });
            sequencer.start();
        } catch (InvalidMidiDataException | IOException | MidiUnavailableException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Experimental. Play notes from data supplied by controller.
     */
    private void midiTrack() {
        // amgthme.mid
        try {
            // Creating a sequence.
            Sequence sequence = new Sequence(Sequence.PPQ, 4);

            Track track = sequence.createTrack();

            // Adding some events to the track
            for (int i = 5; i < (4 * 5) + 5; i += 4) {

                // Add Note On event
                //track.add(makeEvent(144, 1, i, 100, i));
                // Add Note Off event
                //track.add(makeEvent(128, 1, i, 100, i + 2));
                ShortMessage a = new ShortMessage();
                a.setMessage(144, 1, i, 100);
                track.add(new MidiEvent(a, i));

                ShortMessage b = new ShortMessage();
                b.setMessage(128, 1, i, 100);
                track.add(new MidiEvent(b, i + 2));

            }
            // Setting our sequence so that the sequencer can
            // run it on synthesizer
            sequencer.setSequence(sequence);
 
            // Specifies the beat rate in beats per minute.
            sequencer.setTempoInBPM(220);
 
            // Sequencer starts to play notes
            sequencer.start();
 
        } catch (InvalidMidiDataException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }
}
