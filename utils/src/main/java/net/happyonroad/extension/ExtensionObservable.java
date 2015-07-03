package net.happyonroad.extension;

import java.util.Observable;

/**
 * <h1>Extension Observable</h1>
 *
 * @author Jay Xiong
 */
class ExtensionObservable extends Observable{

    synchronized void makeChanged() {
        super.setChanged();
    }

    synchronized void clearChanges() {
        super.clearChanged();
    }

}
