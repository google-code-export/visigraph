/**
 * Main.java
 */
package edu.belmont.mth.visigraph;

import javax.swing.*;
import edu.belmont.mth.visigraph.gui.*;
import edu.belmont.mth.visigraph.models.generators.*;
import edu.belmont.mth.visigraph.settings.*;

/**
 * @author Cameron Behar
 * 
 */
public class Main
{
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
		
		ValidateGraphGenerators();
		
		new MainWindow();
	}
	
	/**
	 * Validates each {@link GraphGeneratorBase} specified in {@link GlobalSettings} to ensure logical consistency among its rules.
	 * <p/>
	 * This method does not return a value, but instead throws an {@link Exception} if an {@link GraphGeneratorBase} implements inconsistent rules.
	 * Because loops, multiple edges, and cycles are not mutually exclusive concepts, we must provide this sanity-check to ensure that no
	 * {@link GraphGeneratorBase} allows inconsistency or ambiguity. The logic used to validate internal consistency follows:
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
	 *             If an {@link GraphGeneratorBase} in the {@link GlobalSettings}'s registry implements inconsistent rules.
	 */
	private static void ValidateGraphGenerators() throws Error
	{
		for (GraphGeneratorBase generator : GlobalSettings.allGraphGenerators)
		{
			if (!generator.areCyclesAllowed().isTrue())
			{
				if (generator.areCyclesAllowed().isForced())
				{
					if (!(!generator.areLoopsAllowed().isTrue() && generator.areLoopsAllowed().isForced()))
						throw new Error("The \"" + generator.getDescription() + "\" generator's default rules are inconsistent: if cycles are disallowed by force, loops must also be disallowed by force.");
					if (!(!generator.areMultipleEdgesAllowed().isTrue() && generator.areMultipleEdgesAllowed().isForced()))
						throw new Error("The \"" + generator.getDescription() + "\" generator's default rules are inconsistent: if cycles are disallowed by force, multiple edges must also be disallowed by force.");
				}
				else
				{
					if (!generator.areLoopsAllowed().isTrue())
						throw new Error("The \"" + generator.getDescription() + "\" generator's default rules are inconsistent: if cycles are disallowed by default, loops must also be disallowed, either by force or by default.");
					if (!generator.areMultipleEdgesAllowed().isTrue())
						throw new Error("The \"" + generator.getDescription() + "\" generator's default rules are inconsistent: if cycles are disallowed by default, multiple edges must also be disallowed, either by force or by default.");
				}
			}
		}
	}
}
