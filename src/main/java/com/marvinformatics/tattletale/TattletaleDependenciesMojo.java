package com.marvinformatics.tattletale;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.DefaultDependencyResolutionRequest;
import org.apache.maven.project.DependencyResolutionException;
import org.apache.maven.project.DependencyResolutionResult;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectDependenciesResolver;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.util.filter.ScopeDependencyFilter;

/**
 * @author Marvin Froeder < marvin at marvinformatics.com >
 * @goal tattletale-dependencies
 * @phase verify
 */
public class TattletaleDependenciesMojo
    extends TattletaleMojo
{

    /**
     *  @component 
     */
    private ProjectDependenciesResolver dependencyResolver;

    /**
     * The maven session.
     * 
     * @parameter expression="${session}"
     * @required
     * @readonly
     */
    private MavenSession session;

    /**
     * The maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * @parameter 
     */
    private List<String> scopes;

    @Override
    public String getSources()
    {
        DefaultDependencyResolutionRequest resolution = new DefaultDependencyResolutionRequest( project, session.getRepositorySession() );
        resolution.setResolutionFilter( new ScopeDependencyFilter( scopes, null ) );
        DependencyResolutionResult resolutionResult;
        try
        {
            resolutionResult = dependencyResolver.resolve( resolution );
        }
        catch ( DependencyResolutionException e )
        {
            getLog().error( "Unable to read project dependencies", e );
            return "";
        }

        List<Dependency> dependencies = resolutionResult.getResolvedDependencies();

        Set<File> directories = new LinkedHashSet<File>();
        for ( Dependency dep : dependencies )
        {
            directories.add( dep.getArtifact().getFile().getAbsoluteFile().getParentFile() );
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
