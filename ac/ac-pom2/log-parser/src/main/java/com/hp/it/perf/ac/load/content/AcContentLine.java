package com.hp.it.perf.ac.load.content;

public class AcContentLine {

	public static enum NextLineStatus {
		StartOfBlock, Unknown, Ready;

		public boolean isReady() {
			return Ready == this;
		}

		boolean isNextBlock() {
			return StartOfBlock == this;
		}

	}

	private String currentLines;
	private String nextLine;
	private AcContentLineInfo lineInfo;
	private NextLineStatus nextLineStatus = NextLineStatus.Unknown;

	protected void setCurrentLines(String currentLines) {
		this.currentLines = currentLines;
	}

	protected void setNextLine(String nextLine) {
		nextLineStatus = NextLineStatus.Ready;
		this.nextLine = nextLine;
	}

	protected void setLineInfo(AcContentLineInfo lineInfo) {
		this.lineInfo = lineInfo;
	}

	/**
	 * true: next line is null (determined); false: undetermined (null next
	 * line) or next line exist
	 * 
	 * @return
	 */
	public boolean isEOB() {
		return nextLineStatus.isNextBlock()
				|| (nextLineStatus.isReady() && nextLine == null);
	}

	public NextLineStatus getNextLineStatus() {
		return nextLineStatus;
	}

	public String getCurrentLines() {
		return currentLines;
	}

	public String getNextLine() {
		return nextLine;
	}

	public AcContentLineInfo getLineInfo() {
		return lineInfo;
	}

	@Override
	public String toString() {
		return String
				.format("AcContentLine: %s%n--> Current Line:%n%s%n--> Next Line(%s):%n%s",
						lineInfo, currentLines, nextLineStatus, nextLine);
	}

	protected void setNextLineStatus(NextLineStatus status) {
		this.nextLineStatus = status;
	}

	public boolean isNextLineUnknown() {
		return nextLineStatus == NextLineStatus.Unknown;
	}

}
