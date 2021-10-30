/*
 *  Copyright (c) 2021, Paladin.ac
 *
 *  All rights reserved.
 *
 *  Author(s):
 *   Marshall Walker
 */

package ac.paladin.auto.model.scan;

import java.util.UUID;
import java.util.function.Consumer;

public interface IScan {

    /**
     * Get the id for this scan.
     *
     * @return scan id
     */
    String getId();

    /**
     * Get the download link for the external scanning software.
     *
     * @return download link
     */
    String getLink();

    /**
     * Get the results link for the external scanning software.
     *
     * @return results link
     */
    String getResultsLink();

    /**
     * Get the player this scan is for.
     *
     * @return target player
     */
    UUID getTargetId();

    /**
     * Get the pin to use in the external scanning software.
     *
     * @return access pin
     */
    String getPin();

    void abort();

    /**
     * Called when the target player runs the external scanning software.
     *
     * @param runnable started
     */
    void onStarted(Runnable runnable);

    /**
     * Called when the target player disconnects from the server.
     *
     * @param runnable aborted
     */
    void onAborted(Runnable runnable);

    /**
     * Called when the scanning software completes the scan on the target player.
     *
     * @param runnable completed
     */
    void onComplete(Runnable runnable);

    /**
     * Called when the external scanning software updates progress.
     *
     * @param progression progress
     */
    void onProgressUpdate(Consumer<Integer> progression);
}
