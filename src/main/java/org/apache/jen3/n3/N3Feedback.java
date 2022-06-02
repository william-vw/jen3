package org.apache.jen3.n3;

import org.apache.jen3.reasoner.rulesys.builtins.n3.N3Builtin;
import org.apache.jen3.shared.JenaException;
import org.apache.jena.atlas.logging.Log;

public class N3Feedback {

	private N3MistakeTypes mistake;
	private FeedbackActions action;
	private FeedbackTypes type;

	private N3FeedbackListener listener;

	public N3Feedback(N3MistakeTypes mistake, FeedbackActions action) {
		if (action != FeedbackActions.NONE)
			throw new RuntimeException("need a feedback-type as well");

		this.mistake = mistake;
		this.action = action;
	}

	public N3Feedback(N3MistakeTypes mistake, N3FeedbackListener listener) {
		this.mistake = mistake;

		this.action = FeedbackActions.CUSTOM;
		this.listener = listener;
	}

	public N3Feedback(N3MistakeTypes mistake, FeedbackTypes type, FeedbackActions action) {
		this.mistake = mistake;
		this.type = type;
		this.action = action;
	}

	public N3MistakeTypes getMistake() {
		return mistake;
	}

	public FeedbackTypes getType() {
		return type;
	}

	public FeedbackActions getAction() {
		return action;
	}

	public N3FeedbackListener getListener() {
		return listener;
	}

	public void overrideAction(N3Feedback with) {
		this.action = with.getAction();
		this.listener = with.getListener();
	}

	public void doDefaultAction(Object... params) {
		doAction(mistake.getMessage(), params);
	}

	public void doActionAt(int msgIdx, Object... params) {
		doAction(mistake.getMessage(msgIdx), params);
	}

	private void doAction(String msg, Object... params) {
		msg = String.format(msg, params);

		switch (action) {

		case LOG:
			switch (type) {
			case WARN:
				Log.warn(N3Builtin.class, msg);
				break;

			case ERROR:
				Log.error(N3Builtin.class, msg);
				break;

			default:
				break;
			}
			break;

		case THROW_EXC:
			throw new JenaException(msg);

		case CUSTOM:
			listener.onFeedback(this, msg);
			break;

		default:
			break;
		}
	}

	public static interface N3FeedbackListener {

		public void onFeedback(N3Feedback feedback, String msg);
	}
}
