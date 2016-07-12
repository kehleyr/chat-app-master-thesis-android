package com.app.charlotte.myapplication.audio;

import android.content.Context;
import android.util.Log;


public class AudioAnalysis {

    private static Context context;
    private static short[] audio_data;
    private static int buffer_size;
    private double mGain = 2500.0 / Math.pow(10.0, 90.0 / 20.0);

    public AudioAnalysis(Context c, short[] audio, int buffer) {
        context = c;
        audio_data = audio;
        buffer_size = buffer;
    }

    /**
     * Get sample Root Mean Squares value. Used to detect silence.
     *
     * @return RMS value
     */

    public double getRMS() {
        double sum = 0d;
        for (short data : audio_data) {
            sum += data;
        }
        double average = sum / audio_data.length;
        double sumMeanSquare = 0d;
        for (short data : audio_data) {
            sumMeanSquare += Math.pow(data - average, 2d);
        }
        double averageMeanSquare = sumMeanSquare / audio_data.length;
        return Math.sqrt(averageMeanSquare);
    }

/*
    //RMS to check if we are in silence
    public boolean isSilent(double rms) {
        double threshold = Double.valueOf(Aware.getSetting(context, Settings.PLUGIN_AMBIENT_NOISE_SILENCE_THRESHOLD));
        return (rms <= threshold);
    }
*/
    /**
     * Get sound frequency in Hz
     *
     * @return Frequency in Hz
     */
    /*
    public double getFrequency() {
        if (audio_data.length == 0) return 0;

        //Create an FFT buffer
        double[] fft_buffer = new double[buffer_size * 2];
        for (int i = 0; i < audio_data.length; i++) {
            fft_buffer[2 * i] = (double) audio_data[i];
            fft_buffer[2 * i + 1] = 0;
        }

        //apply FFT to fill imaginary buffers
        DoubleFFT_1D fft = new DoubleFFT_1D(buffer_size);
        fft.realForward(fft_buffer);

        //Fetch power spectrum (magnitudes) and normalize them
        double[] magnitudes = new double[buffer_size / 2];
        for (int i = 1; i < buffer_size / 2 - 1; i++) {
            double real = fft_buffer[2 * i];
            double imaginary = fft_buffer[2 * i + 1];
            magnitudes[i] = Math.sqrt((real * real) + (imaginary * imaginary));
        }

        //find largest peak in power spectrum (magnitudes)
        double max = -1;
        int max_index = -1;
        for (int i = 0; i < buffer_size / 2 - 1; i++) {
            if (magnitudes[i] > max) {
                max = magnitudes[i];
                max_index = i;
            }
        }
        return 2 * (max_index * 8000 / buffer_size);
    }
*/
    /**
     * Relative ambient noise in dB
     *
     * @return dB level
     * @param recordedShorts
     */
    public double getdB(int recordedShorts) {
        if (audio_data.length == 0) return 0;
        double amplitude = -1;

        for (short data : audio_data) {
            if (amplitude < data) {
                amplitude = data;
            }
        }

        double rms = 0;
        for (int i = 0; i < recordedShorts; i++) {
            rms += audio_data[i]*audio_data[i];
        }
        rms = Math.sqrt(rms/audio_data.length);
        Log.d("TAG", "own computed rms is: "+rms);

        // Compute a smoothed version for less flickering of the display.
        final double rmsdB = 20.0 * Math.log10(mGain * rms);
        Log.d("TAG", "rmsdb: "+rmsdB);

      Log.d("TAG", "amplitude is: "+amplitude+" rms is: "+getRMS());
        double value1= Math.abs(20 * Math.log10(getRMS() / mGain));
        double value2= Math.abs(20 * Math.log10(amplitude / 32768.0));

        Log.d("TAG", "value1 is: "+value1+"value 2 is: "+value2);

        return value2;
    }




}
