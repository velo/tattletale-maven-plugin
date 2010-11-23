package org.sonatype.maven.plugins.tattletale;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

/**
 * @author Marvin Froeder
 * @goal tattletale-dependencies
 * @phase verify
 * @requiresDependencyResolution compile+runtime
 */
public class TattletaleDependenciesMojo
    extends TattletaleMojo
{

    /**
     * The maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    @Override
    public String getSources()
    {
        Set<Artifact> artifacts = project.getArtifacts();

        Set<File> directories = new LinkedHashSet<File>();
        for ( Artifact artifact : artifacts )
        {
            directories.add( artifact.getFile().getAbsoluteFile().getParentFile() );
        }

        final StringBuilder source = new StringBuilder();
        for ( File dir : directories )
        {
            if ( source.length() != 0 )
            {
                source.append( File.pathSeparator );
            }
            source.append( dir.getAbsolutePath() );
        }
        return source.toString();
    }
}
