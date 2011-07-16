using System;
using System.Net;
using System.Data;
using System.Linq;
using System.Text;
using System.Drawing;
using System.Windows.Forms;
using System.ComponentModel;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using System.IO;
using System.Diagnostics;

namespace VisiGraph
{
	public partial class SplashScreen : Form
	{
		private const string _JarDirectoryUrl = @"http://visigraph.googlecode.com/svn/trunk/VisiGraph/jar";
		private const string _JarFileRegex = @"VisiGraph\s\(\d{12}\)\.jar";
		private string _ApplicationDirectory = System.IO.Path.GetDirectoryName( System.Reflection.Assembly.GetExecutingAssembly( ​).Location );

		public SplashScreen ( )
		{
			InitializeComponent( );
		}

		private void SplashScreen_Activated ( object sender, EventArgs e )
		{
			this.Hide( );
		}

		private void SplashScreen_Load ( object sender, EventArgs e )
		{
			this.Hide( );

			bool retry;

			do
			{
				retry = false;

				try
				{
					DownloadLatestJarFile( );
					LaunchLatestJarFile( );
				}
				catch (Exception ex)
				{
					var result = MessageBox.Show( this, "An exception occurred while downloading the latest version of VisiGraph. Would you like to retry?", "Unable to download VisiGraph", MessageBoxButtons.RetryCancel, MessageBoxIcon.Error );
					if (result == System.Windows.Forms.DialogResult.Retry)
						retry = true;
					else if (GetLatestDownloadedJarFilename( ) != null)
							LaunchLatestJarFile( );
				}
			} while (retry);

			this.Close( );
		}

		public string GetLatestDownloadedJarFilename ( )
		{
			var files = Directory.GetFiles( _ApplicationDirectory, "VisiGraph (*).jar", SearchOption.TopDirectoryOnly );
			return (files.Length > 0 ? files[files.Length - 1] : null);
		}

		public string GetLatestUploadedJarFilename ( )
		{
			using (var webClient = new WebClient( ))
			{
				var html = webClient.DownloadString( _JarDirectoryUrl );
				var regex = new Regex( _JarFileRegex, RegexOptions.IgnoreCase | RegexOptions.CultureInvariant | RegexOptions.IgnorePatternWhitespace | RegexOptions.Compiled );
				var matches = regex.Matches( html );
				return (matches.Count > 0 ? matches[matches.Count - 1].Value : null);
			}
		}

		private void DownloadLatestJarFile ( )
		{
			var latestDownloadedJarFilename = GetLatestDownloadedJarFilename( );
			var latestUploadedJarFilename = GetLatestUploadedJarFilename( );

			if (latestDownloadedJarFilename != latestUploadedJarFilename)
				using (var webClient = new WebClient( ))
					webClient.DownloadFile( new Uri( _JarDirectoryUrl + "/" + latestUploadedJarFilename ), _ApplicationDirectory + "\\" + latestUploadedJarFilename );
		}

		private int LaunchLatestJarFile ( )
		{
			using (Process process = Process.Start( new ProcessStartInfo( GetLatestDownloadedJarFilename( ), AppDomain.CurrentDomain.SetupInformation.ActivationArguments.ActivationData != null ? "\"" + String.Join( "\" \"", AppDomain.CurrentDomain.SetupInformation.ActivationArguments.ActivationData ) + "\"" : "" ) ))
			{
				process.WaitForExit( );
				return process.ExitCode;
			}
		}	
	}
}