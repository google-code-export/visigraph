/**
 * GeneratorService.java
 */
package edu.belmont.mth.visigraph.models.generators;

import java.io.*;
import edu.belmont.mth.visigraph.models.*;
import edu.belmont.mth.visigraph.utilities.DebugUtilities;
import bsh.Interpreter;

/**
 * @author Cameron Behar
 *
 */
public class GeneratorService
{	
public final static GeneratorService instance = new GeneratorService();
	
	public final ObservableList<GeneratorBase> generators;
	
	private GeneratorService ( )
	{
		generators = new ObservableList<GeneratorBase>("generators");
		
		generators.add(new EmptyGraph());
		
		File folder = new File("generators");
		if(folder.exists())
		{
			for(String filename : folder.list( new FilenameFilter() { public boolean accept(File dir, String name) { return name.endsWith(".generator"); } } ))
			{
				try
				{
					GeneratorBase generator = (GeneratorBase)new Interpreter().source("generators/" + filename);
					ValidateGraphGenerator(generator);
					generators.add(generator);
				}
				catch (Throwable ex) { DebugUtilities.logException(String.format("An exception occurred while compiling %s.", filename), ex); }
			}
		}
	}
	
	/**
	 * Validates each {@link GeneratorBase} specified in {@link userSettings} to ensure logical consistency among its rules.
	 * <p/>
	 * This method does not return a value, but instead throws an {@link Exception} if an {@link GeneratorBase} implements inconsistent rules.
	 * Because loops, multiple edges, and cycles are not mutually exclusive concepts, we must provide this sanity-check to ensure that no
	 * {@link GeneratorBase} allows inconsistency or ambiguity. The logic used to validate internal consistency follows:
	 * <p/>
	 * <ul>
	 * <li>If cycles are allowed ...
	 * <ul>
	 * <li>... by force OR by default, ...
	 * <ul>
	 * <li>... loops may be allowed OR disallowed, either by force OR by default, AND
	 * <li>... multiple edges may be allowed OR disallowed, either by force OR by default.
	 * </ul>
	 * </ul>
	 * <li>If cycles are disallowed ...
	 * <ul>
	 * <li>... by force, ...
	 * <ul>
	 * <li>... loops must also be disallowed by force, AND
	 * <li>... multiple edges must also be disallowed by force.
	 * </ul>
	 * </ul>
	 * <ul>
	 * <li>... by default, ...
	 * <ul>
	 * <li>... loops must also be disallowed, either by force OR by default, AND
	 * <li>... multiple edges must also be disallowed, either by force OR by default.
	 * </ul>
	 * </ul>
	 * </ul>
	 * 
	 * @author Cameron Behar
	 * @throws Error
	 *             If an {@link GeneratorBase} in the {@link userSettings}'s registry implements inconsistent rules.
	 */
	private static void ValidateGraphGenerator (GeneratorBase generator) throws Error
	{
		if (!generator.areCyclesAllowed().isTrue())
		{
			if (generator.areCyclesAllowed().isForced())
			{
				if (!(!generator.areLoopsAllowed().isTrue() && generator.areLoopsAllowed().isForced()))
					throw new Error("The \"" + generator + "\" generator's default rules are inconsistent: if cycles are disallowed by force, loops must also be disallowed by force.");
				if (!(!generator.areMultipleEdgesAllowed().isTrue() && generator.areMultipleEdgesAllowed().isForced()))
					throw new Error("The \"" + generator + "\" generator's default rules are inconsistent: if cycles are disallowed by force, multiple edges must also be disallowed by force.");
			}
			else
			{
				if (generator.areLoopsAllowed().isTrue())
					throw new Error("The \"" + generator + "\" generator's default rules are inconsistent: if cycles are disallowed by default, loops must also be disallowed, either by force or by default.");
				if (generator.areMultipleEdgesAllowed().isTrue())
					throw new Error("The \"" + generator + "\" generator's default rules are inconsistent: if cycles are disallowed by default, multiple edges must also be disallowed, either by force or by default.");
			}
		}
	}
}
