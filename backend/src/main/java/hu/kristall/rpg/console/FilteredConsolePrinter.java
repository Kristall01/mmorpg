package hu.kristall.rpg.console;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.function.Function;

public class FilteredConsolePrinter extends PrintStream {
	
	private final Function<String, String> converter;
	
	public FilteredConsolePrinter(@NotNull OutputStream out, Function<String, String> converter) {
		super(out);
		this.converter = converter;
	}
	
	
	
	@Override
	public void println(@Nullable String x) {
		super.println(converter.apply(x));
	}
	
	@Override
	public void print(@Nullable String s) {
		super.print(converter.apply(s));
	}
	
}
