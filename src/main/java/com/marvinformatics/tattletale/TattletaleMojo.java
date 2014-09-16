package com.marvinformatics.tattletale;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.DirectoryScanner;
import org.jboss.tattletale.Main;

/**
 * Goal that runs tattletale against a given directory
 * 
 * @author Marvin Froeder < marvin at marvinformatics.com >
 */
@Mojo( name = "tattletale", defaultPhase = LifecyclePhase.VERIFY )
public class TattletaleMojo
    extends AbstractMojo
{
    /**
     * Blacklisted
     */
    @Parameter
    private String[] blacklisted;

    /**
     * Class loader structure
     */
    @Parameter
    private String classloaderStructure;

    /**
     * Configuration
     */
    @Parameter
    private File configuration;

    /**
     * Destination directory
     */
    @Parameter( defaultValue = "${project.build.directory}/tattletale", required = true )
    private File destination;

    /**
     * Excludes
     */
    @Parameter
    private String[] excludes;

    /**
     * Fail on error
     */
    @Parameter( defaultValue = "true" )
    private boolean failOnError;

    /**
     * Fail on info
     */
    @Parameter( defaultValue = "false" )
    private boolean failOnInfo;

    /**
     * Fail on warning
     */
    @Parameter( defaultValue = "true" )
    private boolean failOnWarn;

    /**
     * Filter
     */
    @Parameter
    private File filter;

    /**
     * Profiles
     */
    @Parameter
    private String[] profiles;

    /**
     * Reports
     */
    @Parameter
    private String[] reports;

    /**
     * Scan
     */
    @Parameter
    private String scan;

    /**
     * Source directory
     */
    @Parameter
    private File source;

    /**
     * A source directory that will be scanned for all jars
     */
    @Parameter
    private File sourcesScan;

    /**
     * Whenever should avoid this plugin execution
     */
    @Parameter( defaultValue = "false", property = "tattletale.skip" )
    private boolean skip;

    public String getSources()
    {
        if ( sourcesScan == null )
        {
            return source.getAbsolutePath();
        }
        DirectoryScanner scan = new DirectoryScanner();
        scan.setBasedir( sourcesScan );
        scan.setIncludes( new String[] { "**/*.jar" } );
        scan.addDefaultExcludes();
        scan.scan();

        Set<File> directories = new LinkedHashSet<File>();
        String[] files = scan.getIncludedFiles();
        for ( String file : files )
        {
            directories.add( new File( sourcesScan, file ).getAbsoluteFile().getParentFile() );
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

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( skip )
        {
            getLog().info( "Skipping tattletale" );
            return;
        }

        Main main = new Main();

        main.setSource( getSources() );
        main.setDestination( destination.getAbsolutePath() );

        if ( configuration != null )
        {
            main.setConfiguration( configuration.getAbsolutePath() );
        }

        if ( filter != null )
        {
            main.setFilter( filter.getAbsolutePath() );
        }

        main.setClassLoaderStructure( classloaderStructure );

        if ( reports != null )
        {
            StringBuilder sb = new StringBuilder();
            for ( int i = 0; i < reports.length; i++ )
            {
                sb = sb.append( reports[i] );
                if ( i < reports.length - 1 )
                {
                    sb = sb.append( "," );
                }
            }
            main.setReports( sb.toString() );
        }

        if ( profiles != null )
        {
            StringBuilder sb = new StringBuilder();
            for ( int i = 0; i < profiles.length; i++ )
            {
                sb = sb.append( profiles[i] );
                if ( i < profiles.length - 1 )
                {
                    sb = sb.append( "," );
                }
            }
            main.setProfiles( sb.toString() );
        }

        if ( excludes != null )
        {
            StringBuilder sb = new StringBuilder();
            for ( int i = 0; i < excludes.length; i++ )
            {
                sb = sb.append( excludes[i] );
                if ( i < excludes.length - 1 )
                {
                    sb = sb.append( "," );
                }
            }
            main.setExcludes( sb.toString() );
        }

        if ( blacklisted != null )
        {
            StringBuilder sb = new StringBuilder();
            for ( int i = 0; i < blacklisted.length; i++ )
            {
                sb = sb.append( blacklisted[i] );
                if ( i < blacklisted.length - 1 )
                {
                    sb = sb.append( "," );
                }
            }
            main.setBlacklisted( sb.toString() );
        }

        main.setFailOnInfo( failOnInfo );
        main.setFailOnWarn( failOnWarn );
        main.setFailOnError( failOnError );

        main.setScan( scan );

        getLog().info( "Scanning: " + getSources() );

        try
        {
            main.execute();
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }
}
