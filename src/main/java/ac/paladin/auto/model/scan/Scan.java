/*
 *  Copyright (c) 2021, Paladin.ac
 *
 *  All rights reserved.
 *
 *  Author(s):
 *   Marshall Walker
 */

package ac.paladin.auto.model.scan;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;
import java.util.function.Consumer;

@Accessors(prefix = "m_")
public class Scan implements IScan {

    @Getter
    @Setter
    private String m_id;

    @Getter
    @Setter
    private String m_link;

    @Getter
    @Setter
    private String m_resultsLink;

    @Getter
    @Setter
    private UUID m_targetId;

    @Getter
    @Setter
    private String m_pin;

    private Consumer<Integer> m_progressConsumer;

    private Runnable m_startRunnable;

    private Runnable m_abortRunnable;

    private Runnable m_completeRunnable;

    public void updateProgress(int progress) {
        if (m_progressConsumer != null) {
            m_progressConsumer.accept(progress);
        }
    }

    public void start() {
        m_startRunnable.run();
    }

    public void complete() {
        m_completeRunnable.run();
    }

    @Override
    public void abort() {
        m_abortRunnable.run();
    }

    @Override
    public void onProgressUpdate(Consumer<Integer> progress) {
        this.m_progressConsumer = progress;
    }

    @Override
    public void onStarted(Runnable runnable) {
        this.m_startRunnable = runnable;
    }

    @Override
    public void onAborted(Runnable runnable) {
        this.m_abortRunnable = runnable;
    }

    @Override
    public void onComplete(Runnable runnable) {
        this.m_completeRunnable = runnable;
    }
}

