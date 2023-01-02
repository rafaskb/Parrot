package com.rafaskoberg.gdx.parrot.util;

import com.badlogic.gdx.math.MathUtils;

/** This class helps control the randomness of the shuffling of sounds. If you play the same sound type multiple times,
 * this class can help prevent the same sound type index from playing twice in a row, makes a sound type index more likely to play
 * if it hasn't for a while, etc.*/
public class NonRandomShuffle<T>
{
    private WeightPair<T>[] values;

    private float dropOffMultiplier; // the closer to 0 this is, the more uniform the weights will be. The closer to 1, the more likely something is to roll if it hasn't rolled in a while, and vis versa.

    public NonRandomShuffle(float dropOffMultiplier)
    {
        if(dropOffMultiplier < 0 || dropOffMultiplier > 1)
            throw new IllegalArgumentException("dropOffMultiplier must be in the 0-1 range.");

        this.dropOffMultiplier = dropOffMultiplier;
    }

    public void setValues(T... objects)
    {
        this.values = new WeightPair[objects.length];
        for(int i = 0; i < this.values.length; i ++)
            this.values[i] = new WeightPair(objects[i]);
    }

    public T get()
    {
        if(values.length == 0)
            return null;

        int totalWeight = 0;
        for(int i = 0; i < values.length; i ++)
            totalWeight += values[i].weight;
        int idx = 0;
        for(float r = MathUtils.random(0f, 1f) * totalWeight; idx < values.length - 1; ++idx)
        {
            r -= values[idx].weight;
            if(r <= 0.0)
                break;
        }

        for(int i = 0; i < values.length; i ++)
        {
            if(i == idx)
                values[i].weight *= (1 - dropOffMultiplier);
            else
                values[i].weight += dropOffMultiplier;
        }

        return values[idx].object;
    }
}

class WeightPair<T>
{
    float weight;
    T object;

    public WeightPair(T object)
    {
        this.weight = 1;
        this.object = object;
    }
}